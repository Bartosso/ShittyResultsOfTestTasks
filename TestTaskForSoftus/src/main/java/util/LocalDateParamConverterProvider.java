package util;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.ext.ParamConverter;
import javax.ws.rs.ext.ParamConverterProvider;
import javax.ws.rs.ext.Provider;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Provider
public class LocalDateParamConverterProvider implements ParamConverterProvider {

    //Класс который перехватывает параметр LocalDate в виде строки и парсит её в LocalDate
    DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE;

    @Override
    public <T> ParamConverter<T> getConverter(
            Class<T> rawType, Type genericType, Annotation[] antns) {

        if (LocalDate.class == rawType) {
            return new ParamConverter<T>() {
                @Override
                public T fromString(String string) {

                    try {
                        LocalDate localDate = LocalDate.parse(string, DateTimeFormatter.ISO_DATE);

                        return rawType.cast(localDate);
                    } catch (Exception ex) {
                        return null;
                    }
                }

                @Override
                public String toString(T t) {
                    LocalDate localDate = (LocalDate) t;
                    return formatter.format(localDate);
                }
            };
        }

        return null;
    }
}