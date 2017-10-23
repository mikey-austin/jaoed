package org.jaoed.service;

import java.util.List;

public interface AppBuilder {
    AppReloader getReloader();
    List<Service> getServices();
    App build() throws Exception;
}
