package pianissimo.jflowty.functions.checked;

import java.util.function.*;

@FunctionalInterface
public interface CheckedBiConsumer<Input1, Input2, X extends Exception> extends CheckedBiFunction<Input1, Input2, Void, X> {
	void accept(Input1 input1, Input2 input2) throws X;

	@Override
	default Void apply(Input1 input1, Input2 input2) throws X {
		accept(input1, input2);
		return null;
	}

	static <Input1, Input2, X extends Exception> BiConsumer<Input1, Input2> throwing(
			CheckedBiConsumer<Input1, Input2, X> checkedBiConsumer
	) {
		return (input1, input2) -> {
			try {
				checkedBiConsumer.accept(input1, input2);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		};
	}

	static <Input1, Input2, X extends Exception> CheckedBiConsumer<Input1, Input2, X> mock(
			BiConsumer<Input1, Input2> biConsumer
	) {
		return biConsumer::accept;
	}
}
