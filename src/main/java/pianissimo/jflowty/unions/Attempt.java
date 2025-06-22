package pianissimo.jflowty.unions;

import pianissimo.jflowty.functions.checked.*;
import pianissimo.jflowty.functions.conversions.*;

import java.util.*;

import static pianissimo.jflowty.functions.conversions.SuperConsumer.*;
import static pianissimo.jflowty.functions.conversions.SuperFunction.*;

public abstract class Attempt<TSuccess, TFailure> {

	protected Attempt () {}

	public abstract <U, U1 extends U, U2 extends U> U either (
			SuperFunction<TSuccess, U1> onSuccess,
			SuperFunction<TFailure, U2> onFailure
	);

	public <TO, TI extends TO> TO then (SuperFunction<Attempt<TSuccess, TFailure>, TI> mapper) {
		return mapper.apply(this);
	}

	public static <TSuccess, TFailure> Attempt<TSuccess, TFailure> success (TSuccess success) {
		return new Attempt.Success<>(success);
	}

	public static <TSuccess, TFailure> Attempt<TSuccess, TFailure> failure (TFailure failure) {
		return new Attempt.Failure<>(failure);
	}

	@SuppressWarnings ("unchecked")
	public static <TSuccess, TFailure extends Exception> Attempt<TSuccess, TFailure> of (
			CheckedSupplier<TSuccess, TFailure> supplier
	) {
		try {
			return success(supplier.get());
		} catch (Exception e) {
			return failure((TFailure) e);
		}
	}

	@SuppressWarnings ("unchecked")
	public static <Input, TSuccess, TFailure extends Exception> SuperFunction<Input, Attempt<TSuccess, TFailure>> of (
			CheckedFunction<Input, TSuccess, TFailure> function
	) {
		return input -> {
			try {
				return success(function.apply(input));
			} catch (Exception e) {
				return failure((TFailure) e);
			}
		};
	}

	public boolean isSuccess () {
		return Success.class.isAssignableFrom(this.getClass());
	}

	public boolean isFailure () {
		return Failure.class.isAssignableFrom(this.getClass());
	}

	public <SO> Attempt<SO, TFailure> map (SuperFunction<TSuccess, SO> mapper) {
		return either(success -> success(mapper.apply(success)), Attempt::failure);
	}

	public <FO> Attempt<TSuccess, FO> mapFailure (SuperFunction<TFailure, FO> mapper) {
		return either(Attempt::success, failure -> failure(mapper.apply(failure)));
	}

	public <SO> Attempt<SO, TFailure> flatMap (SuperFunction<TSuccess, Attempt<SO, TFailure>> mapper) {
		return either(mapper, Attempt::failure);
	}

	public <FO> Attempt<TSuccess, FO> flatMapFailure (SuperFunction<TFailure, Attempt<TSuccess, FO>> mapper) {
		return either(Attempt::success, mapper);
	}

	@SuppressWarnings ("unchecked")
	public <SO, X extends Exception> Attempt<SO, TFailure> tryMap (
			CheckedFunction<TSuccess, SO, X> function,
			SuperFunction<X, SO> onException
	) {
		return either(
				success -> {
					try {
						return success(function.apply(success));
					} catch (Exception e) {
						return success(onException.apply((X) e));
					}
				}, Attempt::failure
		);
	}

	@SuppressWarnings ("unchecked")
	public <SO, X extends Exception> Attempt<SO, TFailure> tryFlatMap (
			CheckedFunction<TSuccess, Attempt<SO, TFailure>, X> function,
			SuperFunction<X, Attempt<SO, TFailure>> onException
	) {
		return either(
				success -> {
					try {
						return function.apply(success);
					} catch (Exception e) {
						return onException.apply((X) e);
					}
				}, Attempt::failure
		);
	}

	@SuppressWarnings ("unchecked")
	public <FO, X extends Exception> Attempt<TSuccess, FO> tryMapFailure (
			CheckedFunction<TFailure, FO, X> function,
			SuperFunction<X, FO> onException
	) {
		return either(
				Attempt::success,
				failure -> {
					try {
						return failure(function.apply(failure));
					} catch (Exception e) {
						return failure(onException.apply((X) e));
					}
				}
		);
	}

	@SuppressWarnings ("unchecked")
	public <FO, X extends Exception> Attempt<TSuccess, FO> tryFlatMapFailure (
			CheckedFunction<TFailure, Attempt<TSuccess, FO>, X> function,
			SuperFunction<X, Attempt<TSuccess, FO>> onException
	) {
		return either(
				Attempt::success,
				failure -> {
					try {
						return function.apply(failure);
					} catch (Exception e) {
						return onException.apply((X) e);
					}
				}
		);
	}

	public Attempt<TSuccess, TFailure> recover (
			SuperFunction<TFailure, TSuccess> recovery
	) {
		return either(
				Attempt::success,
				failure -> success(recovery.apply(failure))
		);
	}

	@SuppressWarnings ("unchecked")
	public <X extends Exception> Attempt<TSuccess, TFailure> tryRecover (
			CheckedFunction<TFailure, TSuccess, X> recovery,
			SuperFunction<X, TSuccess> onException
	) {
		return either(
				Attempt::success,
				failure -> {
					try {
						return success(recovery.apply(failure));
					} catch (Exception e) {
						return success(onException.apply((X) e));
					}
				}
		);
	}

	public TSuccess get () {
		return either(identity(), __ -> {throw new UnionTypeException(this);});
	}

	public TSuccess getOr (TSuccess other) {
		return either(identity(), __ -> other);
	}

	public TSuccess getOrFrom (SuperSupplier<TSuccess> otherSupplier) {
		return either(identity(), __ -> otherSupplier.get());
	}

	public TSuccess getOrRecover (SuperFunction<TFailure, TSuccess> recovery) {
		return either(identity(), recovery);
	}

	public TSuccess getOrThrow () {
		return either(
				identity(), failure -> {
					if (Exception.class.isAssignableFrom(failure.getClass()))
						throw new RuntimeException((Exception) failure);
					throw new RuntimeException(failure.toString());
				}
		);
	}

	public <X extends Exception> TSuccess getOrThrow (X exception) {
		return either(identity(), __ -> {throw new RuntimeException(exception);});
	}

	public <X extends Exception> TSuccess getOrThrow (SuperSupplier<X> exceptionSupplier) {
		return either(identity(), __ -> {throw new RuntimeException(exceptionSupplier.get());});
	}

	public <X extends Exception> TSuccess getOrThrow (
			SuperFunction<TFailure, X> failureToExceptionMapper
	) {
		return either(
				identity(),
				failure -> {throw new RuntimeException(failureToExceptionMapper.apply(failure));}
		);
	}

	public TFailure getFailure () {
		return either(__ -> {throw new UnionTypeException(this);}, identity());
	}

	public TFailure getFailureOr (TFailure otherFailure) {
		return either(__ -> otherFailure, identity());
	}

	public TFailure getFailureOrFrom (SuperSupplier<TFailure> otherFailureSupplier) {
		return either(__ -> otherFailureSupplier.get(), identity());
	}

	public Attempt<TSuccess, TFailure> filter (SuperPredicate<TSuccess> predicate, TFailure onFail) {
		return either(
				success -> predicate.test(success) ? success(success) : failure(onFail),
				Attempt::failure
		);
	}

	public Attempt<TSuccess, TFailure> filter (SuperPredicate<TSuccess> predicate, SuperSupplier<TFailure> onFail) {
		return either(
				success -> predicate.test(success) ? success(success) : failure(onFail.get()),
				Attempt::failure
		);
	}

	public Attempt<TSuccess, TFailure> filter (
			SuperPredicate<TSuccess> predicate,
			SuperFunction<TSuccess, TFailure> failureMapper
	) {
		return either(
				success -> predicate.test(success) ? success(success) : failure(failureMapper.apply(success)),
				Attempt::failure
		);
	}

	public Attempt<TSuccess, TFailure> ifSuccessPeek (SuperConsumer<TSuccess> consumer) {
		return either(success -> success(peek(consumer, success)), Attempt::failure);
	}

	public Attempt<TSuccess, TFailure> ifFailurePeek (SuperConsumer<TFailure> consumer) {
		return either(Attempt::success, failure -> failure(peek(consumer, failure)));
	}

	public void ifSuccessDo (SuperConsumer<TSuccess> consumer) {
		either(consumer, __ -> null);
	}

	public void ifFailureDo (SuperConsumer<TFailure> consumer) {
		either(__ -> null, consumer);
	}

	private static class Success<TSuccess, TFailure> extends Attempt<TSuccess, TFailure> {
		private final TSuccess value;

		private Success (TSuccess value) {
			this.value = value;
		}

		@Override
		public <U, U1 extends U, U2 extends U> U either (
				SuperFunction<TSuccess, U1> onSuccess,
				SuperFunction<TFailure, U2> onFailure
		) {
			return onSuccess.apply(value);
		}

		@Override public boolean equals (Object o) {
			if (o == null || getClass() != o.getClass()) return false;
			Success<?, ?> success = (Success<?, ?>) o;
			return Objects.equals(value, success.value);
		}

		@Override public int hashCode () {
			return Objects.hashCode(value);
		}

		@Override public String toString () {
			return String.format("Success(%s)", value);
		}
	}

	private static class Failure<TSuccess, TFailure> extends Attempt<TSuccess, TFailure> {
		private final TFailure value;

		private Failure (TFailure value) {
			this.value = value;
		}

		@Override
		public <U, U1 extends U, U2 extends U> U either (
				SuperFunction<TSuccess, U1> onSuccess,
				SuperFunction<TFailure, U2> onFailure
		) {
			return onFailure.apply(value);
		}

		@Override public boolean equals (Object o) {
			if (o == null || getClass() != o.getClass()) return false;
			Failure<?, ?> failure = (Failure<?, ?>) o;
			return Objects.equals(value, failure.value);
		}

		@Override public int hashCode () {
			return Objects.hashCode(value);
		}

		@Override public String toString () {
			return String.format("Failure(%s)", value);
		}
	}
}
