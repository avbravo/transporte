/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.avbravo.transporte.timer;

import java.util.concurrent.atomic.AtomicBoolean;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.inject.Singleton;

/**
 *
 * @author avbravo
 */
@Singleton
public class WorkerBean {
 private AtomicBoolean busy = new AtomicBoolean(false);
 
    @Lock(LockType.READ)
    public void doTimerWork() throws InterruptedException {
        if (!busy.compareAndSet(false, true)) {
            return;
        }
        try {
            Thread.sleep(20000L);
        } finally {
            busy.set(false);
        }   
}
}
