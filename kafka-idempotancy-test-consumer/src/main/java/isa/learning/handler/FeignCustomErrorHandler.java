package isa.learning.handler;


import isa.learning.exception.ErrorDto;
import isa.learning.exception.RecordNotFoundException;
import isa.learning.logger.MainLogger;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Response;
import feign.codec.ErrorDecoder;
import java.io.IOException;
import javax.validation.ValidationException;
import org.springframework.stereotype.Component;

@Component
public class FeignCustomErrorHandler implements ErrorDecoder {
    private static final MainLogger LOGGER = MainLogger.getLogger(FeignCustomErrorHandler.class);
    public static final String ERR_MSG_TEMP = "Got status %d while reading %s with message: %s";
    private final ErrorDecoder defaultErrorDecoder = new Default();

    @Override
    public Exception decode(String methodKey, Response response) {
        if (response.status() == 400) {
            return new ValidationException(getErrorDetail(response), null);
        }
        if (response.status() == 404) {
            return new RecordNotFoundException(getErrorDetail(response));
        }

        return defaultErrorDecoder.decode(methodKey, response);
    }

    private String getErrorDetail(Response response) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
        try {
            if (response.body() != null) {
                ErrorDto dto = objectMapper.readValue(response.body().asReader(), ErrorDto.class);
                if (dto != null) {
                    return dto.getErrorDetail();
                }
            }
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        }
        return null;
    }
}
