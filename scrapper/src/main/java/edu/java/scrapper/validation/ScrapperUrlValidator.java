package edu.java.scrapper.validation;

import edu.java.scrapper.configuration.ApplicationConfig;
import edu.java.scrapper.exceptions.UnsupportedLinkException;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.net.URI;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.beans.factory.annotation.Autowired;

public class ScrapperUrlValidator implements ConstraintValidator<ScrapperUrl, URI> {
    @Autowired
    private ApplicationConfig config;

    @Override
    public boolean isValid(URI value, ConstraintValidatorContext context) {
        if (value == null) {
            return false;
        }

        List<String> allowedUrlPatterns = config.allowedUrlPatterns();
        for (String p : allowedUrlPatterns) {
            Pattern pattern = Pattern.compile(p);
            Matcher matcher = pattern.matcher(value.toString());

            if (matcher.matches()) {
                return true;
            }
        }
        throw new UnsupportedLinkException(value);
    }
}
