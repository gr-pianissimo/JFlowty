package pianissimo.jflowty.functions.checked;

import java.util.function.*;

@FunctionalInterface
public interface CheckedBiPredicate<Input1, Input2, X extends Exception> extends CheckedBiFunction<Input1, Input2, Boolean, X> {
	boolean test(Input1 input1, Input2 input2) throws X;

	@Override
	default Boolean apply(Input1 input1, Input2 input2) throws X {
		return test(input1, input2);
	}

	static <Input1, Input2, X extends Exception> BiPredicate<Input1, Input2> throwing(CheckedBiPredicate<Input1, Input2, X> checkedBiPredicate) {
		return (input1, input2) -> {
			try {
				return checkedBiPredicate.test(input1, input2);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		};
	}

	static <Input, Output, X extends Exception> CheckedBiPredicate<Input, Output, X> mock(BiPredicate<Input, Output> biPredicate) {
		return biPredicate::test;
	}
}
