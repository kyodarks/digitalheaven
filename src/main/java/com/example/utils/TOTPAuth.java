package com.example.utils;

import java.time.Duration;

import com.bastiaanjansen.otp.HMACAlgorithm;
import com.bastiaanjansen.otp.SecretGenerator;
import com.bastiaanjansen.otp.TOTPGenerator;

public class TOTPAuth {
    private final byte[] secret; 
    private TOTPGenerator generator;

    public TOTPAuth(int codeLifetime){
        secret = SecretGenerator.generate();

        generator = new TOTPGenerator.Builder(secret)
        .withHOTPGenerator(builder -> {
            builder.withPasswordLength(6);
            builder.withAlgorithm(HMACAlgorithm.SHA512);
        })
        .withPeriod(Duration.ofSeconds(codeLifetime))
        .build();
    }

    public String reserveCode(){
        return generator.now();
    }

    public boolean verify(String code){return generator.verify(code);}
}
