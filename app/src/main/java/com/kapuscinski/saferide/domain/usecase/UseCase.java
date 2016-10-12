/*
 * Created by Adrian Kapuscinski kapuscinskiadrian@gmail.com
 */

package com.kapuscinski.saferide.domain.usecase;

public interface UseCase<Listener> {

    void setListener(Listener listener);

    void execute();
}
