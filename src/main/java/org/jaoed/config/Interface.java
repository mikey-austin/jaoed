package org.jaoed.config;

import java.util.List;

import org.jaoed.config.ConfigVisitor;
import org.jaoed.config.Section;
import org.jaoed.config.Logger;

class Interface implements Section {
    private String networkInterface;
    private Mtu mtu;
    private Logger logger;
    private Logger.Level logLevel;

    public Interface() {}

    public void acceptVisitor(ConfigVisitor visitor) {
        visitor.visitInterface(this);
    }

    public class Mtu {
        private int mtu;
        private boolean auto;

        public Mtu() {
            this.mtu = 0;
            this.auto = true;
        }

        public Mtu(int mtu) {
            this.mtu = mtu;
            this.auto = false;
        }

        public boolean isAuto() {
            return this.auto;
        }

        public int getMtu() {
            return this.mtu;
        }
    }
}
