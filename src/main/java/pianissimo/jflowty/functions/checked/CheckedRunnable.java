package pianissimo.jflowty.functions.checked;

@FunctionalInterface
public interface CheckedRunnable<X extends Exception> extends CheckedFunction<Void, Void, X> {
	void run() throws X;

	@Override
	default Void apply(Void __) throws X {
		run();
		return null;
	}

	static <X extends Exception> CheckedRunnable<X> mock(Runnable runnable) {
		return runnable::run;
	}
}
