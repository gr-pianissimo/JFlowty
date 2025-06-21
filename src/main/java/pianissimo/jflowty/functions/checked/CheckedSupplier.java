package pianissimo.jflowty.functions.checked;

import java.util.function.*;

@FunctionalInterface
public interface CheckedSupplier<Output, X extends Exception> extends CheckedFunction<Void, Output, X> {
	Output get() throws X;

	@Override
	default Output apply(Void __) throws X {
		return get();
	}

	static <Output, X extends Exception> Supplier<Output> throwing(CheckedSupplier<Output, X> checkedSupplier) {
		return () -> {
			try {
				return checkedSupplier.get();
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		};
	}

	static <Output, X extends Exception> CheckedSupplier<Output, X> mock(Supplier<Output> supplier) {
		return supplier::get;
	}
}
