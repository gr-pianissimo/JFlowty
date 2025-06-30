package pianissimo.jflowty.unions;

import pianissimo.jflowty.functions.checked.*;
import pianissimo.jflowty.functions.conversions.*;

import java.util.*;

import static pianissimo.jflowty.functions.conversions.SuperConsumer.*;
import static pianissimo.jflowty.functions.conversions.SuperFunction.*;

public abstract class Option<T> {

	protected Option () {}

	public abstract <U, U1 extends U, U2 extends U> U either (SuperFunction<T, U1> onSome, SuperSupplier<U2> onEmpty);

	public <U, U2 extends U> U then (SuperFunction<Option<T>, U2> mapper) {
		return mapper.apply(this);
	}

	public static <T> Option<T> of (T value) {
		if (Objects.nonNull(value)) {
			return new Option.Some<>(value);
		}
		return new Option.Empty<>();
	}

	@SuppressWarnings ("OptionalUsedAsFieldOrParameterType")
	public static <T> Option<T> of (Optional<T> optional) {
		return optional.map(Option::of).orElseGet(Option::empty);
	}

	@SuppressWarnings("unchecked")
	public static <T> Option<T> empty () {
		return (Option<T>) Empty.EMPTY;
	}

	public static <T, X extends Exception> Option<T> ofTry (CheckedSupplier<T, X> supplier) {
		try {
			return of(supplier.get());
		} catch (Exception e) {
			return empty();
		}
	}

	public static <T> Option<T> ofOptimisticTry (SuperSupplier<T> supplier) {
		try {
			return of(supplier.get());
		} catch (Exception e) {
			return empty();
		}
	}

	public boolean isPresent () {
		return Some.class.isAssignableFrom(this.getClass());
	}

	public boolean isEmpty () {
		return Empty.class.isAssignableFrom(this.getClass());
	}

	public <O> Option<O> map (SuperFunction<T, O> mapper) {
		return this.either(input -> Option.of(mapper.apply(input)), Option::empty);
	}

	public <O> Option<O> flatMap (SuperFunction<T, Option<O>> mapper) {
		return this.either(mapper, Option::empty);
	}

	public <O, X extends Exception> Option<O> tryMap (CheckedFunction<T, O, X> function) {
		return either(
				some -> {
					try {
						return of(function.apply(some));
					} catch (Exception e) {
						return empty();
					}
				}, Option::empty
		);
	}

	@SuppressWarnings ("unchecked")
	public <O, X extends Exception> Option<O> tryMap (CheckedFunction<T, O, X> function, SuperFunction<X, O> recovery) {
		return either(
				some -> {
					try {
						return of(function.apply(some));
					} catch (Exception e) {
						return of(recovery.apply((X) e));
					}
				}, Option::empty
		);
	}

	public <O, X extends Exception> Option<O> tryFlatMap (CheckedFunction<T, Option<O>, X> function) {
		return either(
				some -> {
					try {
						return function.apply(some);
					} catch (Exception e) {
						return empty();
					}
				}, Option::empty
		);
	}

	@SuppressWarnings ("unchecked")
	public <O, X extends Exception> Option<O> tryFlatMap (
			CheckedFunction<T, Option<O>, X> function,
			SuperFunction<X, Option<O>> recovery
	) {
		return either(
				some -> {
					try {
						return function.apply(some);
					} catch (Exception e) {
						return recovery.apply((X) e);
					}
				}, Option::empty
		);
	}

	public Option<T> recover (SuperSupplier<T> recovery) {
		return either(Option::of, () -> of(recovery.get()));
	}

	public <X extends Exception> Option<T> tryRecover (CheckedSupplier<T, X> recovery) {
		return either(
				Option::of,
				() -> {
					try {
						return of(recovery.get());
					} catch (Exception e) {
						return empty();
					}
				}
		);
	}

	@SuppressWarnings ("unchecked")
	public <X extends Exception> Option<T> tryRecover (
			CheckedSupplier<T, X> recovery,
			SuperFunction<X, T> onException
	) {
		return either(
				Option::of,
				() -> {
					try {
						return of(recovery.get());
					} catch (Exception e) {
						return of(onException.apply((X) e));
					}
				}
		);
	}

	public T get () {
		return either(identity(), () -> {throw new UnionTypeException(this);});
	}

	public T getOr (T other) {
		return either(identity(), () -> other);
	}

	public T getOrFrom (SuperSupplier<T> otherSupplier) {
		return either(identity(), otherSupplier);
	}

	public <X extends Exception> T getOrThrow (X exception) {
		return either(identity(), () -> {throw new RuntimeException(exception);});
	}

	public <X extends Exception> T getOrThrow (SuperSupplier<X> exceptionSupplier) {
		return either(identity(), () -> {throw new RuntimeException(exceptionSupplier.get());});
	}

	public Option<T> filter (SuperPredicate<T> predicate) {
		return either(
				some -> predicate.test(some) ? of(some) : empty(),
				Option::empty
		);
	}

	public Option<T> ifPresentPeek (SuperConsumer<T> consumer) {
		return either(
				some -> of(peek(consumer, some)),
				Option::empty
		);
	}

	public Option<T> ifEmptyPeek (SuperRunnable runnable) {
		return either(
				Option::of,
				() -> {runnable.run(); return empty();}
		);
	}

	public void ifPresentDo (SuperConsumer<T> consumer) {
		either(consumer, () -> null);
	}

	public void ifEmptyDo (SuperRunnable runnable) {
		either(__ -> null, runnable);
	}

	private static class Some<T> extends Option<T> {
		private final T value;

		private Some (T value) {
			this.value = value;
		}

		@Override
		public <U, U1 extends U, U2 extends U> U either (SuperFunction<T, U1> onSome, SuperSupplier<U2> onEmpty) {
			return onSome.apply(value);
		}

		@Override public String toString () {
			return "Some[" + value + ']';
		}

		@Override public boolean equals (Object o) {
			if (o == null || getClass() != o.getClass()) return false;
			Some<?> some = (Some<?>) o;
			return Objects.equals(value, some.value);
		}

		@Override public int hashCode () {
			return Objects.hashCode(value);
		}
	}

	private static class Empty<T> extends Option<T> {
		private static final Empty<?> EMPTY = new Empty<>();

		private Empty () {}

		@Override
		public <U, U1 extends U, U2 extends U> U either (SuperFunction<T, U1> onSome, SuperSupplier<U2> onEmpty) {
			return onEmpty.get();
		}

		@Override public String toString () {
			return "Empty";
		}

		@Override public boolean equals (Object o) {
			if (!Empty.class.equals(o.getClass())) {
				return false;
			}
			return o == EMPTY && this == EMPTY;
		}

		@Override public int hashCode () {
			return Objects.hash(EMPTY);
		}
	}
}
