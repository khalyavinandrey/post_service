package faang.school.postservice.util.validator;

import faang.school.postservice.util.exception.DataValidationException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PostControllerValidatorTest {

    private PostControllerValidator validator;

    @BeforeEach
    void setUp() {
        validator = new PostControllerValidator();
    }

    @Test
    void validateToPublish_IdIsGreaterThanZero_ShouldNotThrowException() {
        Long id = 1L;

        Assertions.assertDoesNotThrow(() -> validator.validateToPublish(id));
    }

    @Test
    void validateToPublish_IdIsLowerThanOne_ShouldThrowException() {
        Long id = 0L;

        DataValidationException e = Assertions.assertThrows(DataValidationException.class,
                () -> validator.validateToPublish(id));
        Assertions.assertEquals("Id should be greater than 0", e.getMessage());
    }
}