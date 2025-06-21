package pianissimo.jflowty.functions.checked;

import java.util.function.*;

@FunctionalInterface
public interface CheckedPredicate<Input, X extends Exception> extends CheckedFunction<Input, Boolean, X> {
	boolean test(Input input) throws X;

	@Override
	default Boolean apply(Input input) throws X {
		return test(input);
	}

	static <Input, X extends Exception> Predicate<Input> throwing(CheckedPredicate<Input, X> checkedPredicate) {
		return input -> {
			try {
				return checkedPredicate.test(input);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		};
	}


	static <Input, Output, X extends Exception> CheckedFunction<Input, Output, X> mock(Function<Input, Output> function) {
		return function::apply;
	}
}
