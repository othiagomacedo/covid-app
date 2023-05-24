package covid.application.api.anotations;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.constraints.Pattern;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.CLASS)
@Constraint(validatedBy = {})
@Pattern(regexp = "\\w{3}", message = "Formato de Sigla inválido. A Sigla deve conter 3 caracteres.")
public @interface Sigla {
    String message() default "Formato de Sigla inválido. A Sigla deve conter 3 caracteres.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
