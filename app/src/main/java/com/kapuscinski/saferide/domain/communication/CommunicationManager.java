/*
 * Created by Adrian Kapuscinski kapuscinskiadrian@gmail.com
 */

package com.kapuscinski.saferide.domain.communication;

import com.kapuscinski.saferide.domain.entity.Damage;

public interface CommunicationManager {

    void broadcastDamageDetected(Damage damage);

    void broadcastOrientationChange(boolean correctOrientation);

    void broadcastSensorsNotAvailable();

    void startDamageDetectionService();

    void stopDamageDetectionService();

    void setDamageDetectionServiceActive(boolean active);

    boolean isDamageDetectionServiceActive();

    void addListener(CommunicationListener listener);

    void removeListener(CommunicationListener listener);
}
