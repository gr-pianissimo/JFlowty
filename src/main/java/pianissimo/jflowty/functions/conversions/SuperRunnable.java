package pianissimo.jflowty.functions.conversions;

import java.util.*;

@FunctionalInterface
public interface SuperRunnable extends Runnable, SuperSupplier<Void>, SuperConsumer<Void> {
	@Override void run ();

	@Override default Void apply (Void __) {
		run();
		return null;
	}

	@Override default Void get () {
		run();
		return null;
	}

	@Override default void accept (Void __) {
		run();
	}

	static SuperRunnable from (Runnable runnable) {
		Objects.requireNonNull(runnable);
		return (SuperRunnable) runnable;
	}
}
