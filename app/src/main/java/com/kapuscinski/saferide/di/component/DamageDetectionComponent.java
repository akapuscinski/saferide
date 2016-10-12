/*
 * Created by Adrian Kapuscinski kapuscinskiadrian@gmail.com
 */

package com.kapuscinski.saferide.di.component;

import com.kapuscinski.saferide.di.module.DamageDetectionModule;
import com.kapuscinski.saferide.di.scope.ServiceScope;
import com.kapuscinski.saferide.presentation.view.service.DamageDetectionService;

import dagger.Component;

@ServiceScope
@Component(dependencies = ApplicationComponent.class, modules = DamageDetectionModule.class)
public interface DamageDetectionComponent {

    void inject(DamageDetectionService service);
}
