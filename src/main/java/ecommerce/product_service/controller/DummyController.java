package ecommerce.product_service.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/products")
@Slf4j
public class DummyController {

    @GetMapping("/msg")
    public String dummyMessage() {
        log.info("===dummy message===");
        return "dummy message";
    }
}
