
package com.emission.calculator.geolocation.domain;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Matrix {

    private List<List<Double>> locations ;
    private List<String> metrics;
    private String units;

}
