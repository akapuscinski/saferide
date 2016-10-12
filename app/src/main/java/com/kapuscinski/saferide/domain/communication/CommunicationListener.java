/*
 * Created by Adrian Kapuscinski kapuscinskiadrian@gmail.com
 */

package com.kapuscinski.saferide.domain.communication;

import com.kapuscinski.saferide.domain.entity.Damage;

public interface CommunicationListener {

    void onDamageDetected(Damage damage);

    void onSensorsNotAvailable();

    void onOrientationChange(boolean correctOrientation);
}
