/*
 * Created by Adrian Kapuscinski kapuscinskiadrian@gmail.com
 */

package com.kapuscinski.saferide.domain.usecase;

import android.support.annotation.Nullable;

import com.kapuscinski.saferide.domain.Constants;
import com.kapuscinski.saferide.domain.damage.DamageDownloader;
import com.kapuscinski.saferide.domain.entity.Area;
import com.kapuscinski.saferide.domain.entity.Damage;
import com.kapuscinski.saferide.domain.entity.Damage_Table;
import com.kapuscinski.saferide.domain.entity.Region;
import com.kapuscinski.saferide.domain.threading.MainThreadExecutor;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import javax.inject.Inject;

/**
 * This use case handles getting damage data from server.
 * <p>
 *     Based on region size it downloads damage data or area data and notifies
 *     listener if set and download operation is completed.
 * </p>
 */
public class GetDamageUseCase extends BaseAsyncUseCase<GetDamageUseCase.Listener> {

    public interface Listener {

        void onDamageReceived(@Nullable List<Damage> damageList);

        void onClusterReceived(List<Area> areaList);
    }

    private DamageDownloader downloader;
    private Region region;
    private List<Region> areaRegions;
    private List<Future<Area>> areaFutures = new ArrayList<>();

    @Inject
    public GetDamageUseCase(ExecutorService executorService, MainThreadExecutor
            mainThreadExecutor, DamageDownloader downloader) {
        this.executor = executorService;
        this.mainThreadExecutor = mainThreadExecutor;
        this.downloader = downloader;
    }

    @Override
    public void run() {
        if (region.getSize() >= Constants.Damage.MINIMAL_CLUSTER_SIZE) {
            for (Future<Area> areaFuture : areaFutures) {
                areaFuture.cancel(true);
            }
            areaFutures.clear();

            for (Region r : areaRegions) {
                areaFutures.add(executor.submit(new AreaFuture(r)));
            }

            final List<Area> areas = new ArrayList<>();
            for (Future<Area> future : areaFutures) {
                try {
                    areas.add(future.get());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }
            post(new Runnable() {
                @Override
                public void run() {
                    listener.onClusterReceived(areas);
                }
            });
        } else {
            List<Damage> damageList = downloader.downloadDamage(getRegion());

            if (damageList == null) {
                damageList = retrieveFromDatabase(getRegion());
            }

            final List<Damage> finalDamageList = damageList;
            post(new Runnable() {
                @Override
                public void run() {
                    listener.onDamageReceived(finalDamageList);
                }
            });
        }
    }

    private class AreaFuture implements Callable<Area> {

        private Region region;

        public AreaFuture(Region region) {
            this.region = region;
        }

        @Override
        public Area call() throws Exception {
            Area area = downloader.downloadArea(region);

            if (area == null) {
                List<Damage> damages = retrieveFromDatabase(region);
                if (damages.size() < 1)
                    return new Area(0, region);

                area = new Area(damages.size(), region);
            }
            return area;
        }
    }

    private List<Damage> retrieveFromDatabase(Region region) {
        return SQLite.select().from(Damage.class)
                .where(Damage_Table.latitude.between(region.getBottomLat()).and
                        (region.getTopLat()))
                .and(Damage_Table.longitude.between(region.getLeftLon()).and
                        (region.getRightLon()))
                .queryList();
    }

    public Region getRegion() {
        return region;
    }

    public void setRegion(Region region) {
        this.region = region;
    }

    public List<Region> getAreaRegions() {
        return areaRegions;
    }

    public void setAreaRegions(List<Region> areaRegions) {
        this.areaRegions = areaRegions;
    }
}
