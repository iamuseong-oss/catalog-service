package com.polarbookshop;

import org.springframework.web.bind.annotation.RestController;

import com.polarbookshop.config.PolarProperties;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequiredArgsConstructor
public class HomeController {

  private final PolarProperties polarProperties;

  @GetMapping("/")
  public String getGreeting() {
    return polarProperties.getGreeting();
  }

}
