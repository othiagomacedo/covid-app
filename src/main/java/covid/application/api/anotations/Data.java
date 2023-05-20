package covid.application.api.anotations;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.constraints.Pattern;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {})
@Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}", message = "Formato de data inválido. Utilize o formato YYYY-MM-DD.")
public @interface Data {
    String message() default "Formato de data inválido. Utilize o formato YYYY-MM-DD.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
