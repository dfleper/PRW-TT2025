package es.prw.config.dev;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Profile("dev")
@Controller
public class DevErrorController {

  @GetMapping("/dev/boom")
  public String boom() {
    throw new RuntimeException("Test 500 (dev)");
  }
}
