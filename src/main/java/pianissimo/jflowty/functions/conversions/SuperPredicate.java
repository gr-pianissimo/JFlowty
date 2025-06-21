package pianissimo.jflowty.functions.conversions;

import java.util.*;
import java.util.function.*;

@FunctionalInterface
public interface SuperPredicate<Input> extends Predicate<Input>, Supplier<Boolean>, SuperFunction<Input, Boolean> {
	@Override
	boolean test(Input input);

	@Override
	default Boolean apply(Input input) {
		Objects.requireNonNull(input);
		return test(input);
	}

	@Override
	default Boolean get() {
		return test(null);
	}

	static <Input> SuperPredicate<Input> from(Predicate<Input> predicate) {
		Objects.requireNonNull(predicate);
		return (SuperPredicate<Input>) predicate;
	}
}
