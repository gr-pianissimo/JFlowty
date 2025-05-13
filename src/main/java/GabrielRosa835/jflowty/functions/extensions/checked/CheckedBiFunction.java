package GabrielRosa835.jflowty.functions.extensions.checked;

import java.util.function.*;

@FunctionalInterface
public interface CheckedBiFunction<Input1, Input2, Output, X extends Exception> {
	Output apply(Input1 input1, Input2 input2) throws X;

	static <Input1, Input2, Output, X extends Exception> BiFunction<Input1, Input2, Output> throwingRuntime(
			CheckedBiFunction<Input1, Input2, Output, X> checkedBiFunction
	) {
		return (input1, input2) -> {
			try {
				return checkedBiFunction.apply(input1, input2);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		};
	}

	static <Input1, Input2, Output, X extends Exception> CheckedBiFunction<Input1, Input2, Output, X> mockThrowing(
			BiFunction<Input1, Input2, Output> biFunction
	) {
		return biFunction::apply;
	}
}
