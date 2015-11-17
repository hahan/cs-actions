package io.cloudslang.content.entities;

import io.cloudslang.content.constants.ErrorMessages;
import org.apache.commons.lang3.StringUtils;

/**
 * Created by Mihai Tusa.
 * 10/30/2015.
 */
public enum Protocol {
    HTTP,
    HTTPS;

    public static String getValue(String input) throws Exception {
        if (StringUtils.isBlank(input)) {
            return Protocol.HTTPS.toString();
        }
        try {
            return Protocol.valueOf(input.toUpperCase()).toString();
        } catch (IllegalArgumentException iae) {
            throw new Exception(ErrorMessages.UNSUPPORTED_PROTOCOL);
        }
    }
}