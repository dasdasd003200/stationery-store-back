package com.stationerystore.stationery_store.shared.graphql;

import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

@Controller
class PingController {

    @QueryMapping
    String ping() {
        return "pong";
    }
}
