package pianissimo.jflowty.functions.checked;

import java.util.function.*;

@FunctionalInterface
public interface CheckedConsumer<Input, X extends Exception> extends CheckedFunction<Input, Void, X> {
	void accept(Input input) throws X;

	@Override
	default Void apply(Input input) throws X {
		accept(input);
		return null;
	}

	static <Input, X extends Exception> Consumer<Input> throwing(CheckedConsumer<Input, X> checkedConsumer) {
		return input -> {
			try {
				checkedConsumer.accept(input);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		};
	}

	static <Input, X extends Exception> CheckedConsumer<Input, X> mock(Consumer<Input> consumer) {
		return consumer::accept;
	}
}
