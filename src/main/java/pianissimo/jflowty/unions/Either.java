package pianissimo.jflowty.unions;

import pianissimo.jflowty.functions.checked.*;
import pianissimo.jflowty.functions.conversions.*;

import java.util.*;
import java.util.function.*;

import static pianissimo.jflowty.functions.conversions.SuperConsumer.*;
import static pianissimo.jflowty.functions.conversions.SuperFunction.*;

public abstract class Either<TLeft, TRight> {

	protected Either () {}

	public abstract <T, T1 extends T, T2 extends T> T either (
			SuperFunction<TLeft, T1> onLeft,
			SuperFunction<TRight, T2> onRight
	);

	public <T, T2 extends T> T then (SuperFunction<Either<TLeft, TRight>, T2> mapper) {
		return mapper.apply(this);
	}

	public static <TLeft, TRight> Either<TLeft, TRight> left (TLeft left) {
		return new Either.Left<>(left);
	}

	public static <TLeft, TRight> Either<TLeft, TRight> right (TRight right) {
		return new Either.Right<>(right);
	}

	public boolean isLeft () {
		return Left.class.isAssignableFrom(this.getClass());
	}

	public boolean isRight () {
		return Right.class.isAssignableFrom(this.getClass());
	}

	public <LO> Either<LO, TRight> mapLeft (SuperFunction<TLeft, LO> onLeft) {
		return either(left -> left(onLeft.apply(left)), Either::right);
	}

	public <RO> Either<TLeft, RO> mapRight (SuperFunction<TRight, RO> onRight) {
		return either(Either::left, right -> right(onRight.apply(right)));
	}

	public <LO> Either<LO, TRight> flatMapLeft (SuperFunction<TLeft, Either<LO, TRight>> onLeft) {
		return either(onLeft, Either::right);
	}

	public <RO> Either<TLeft, RO> flatMapRight (SuperFunction<TRight, Either<TLeft, RO>> onRight) {
		return either(Either::left, onRight);
	}

	@SuppressWarnings ("unchecked")
	public <LO, X extends Exception> Either<LO, TRight> tryMapLeft (
			CheckedFunction<TLeft, LO, X> onLeft,
			SuperFunction<X, LO> onException
	) {
		return either(
				left -> {
					try {
						return left(onLeft.apply(left));
					} catch (Exception e) {
						return left(onException.apply((X) e));
					}
				}, Either::right
		);
	}

	@SuppressWarnings("unchecked")
	public <RO, X extends Exception> Either<TLeft, RO> tryMapRight (
			CheckedFunction<TRight, RO, X> onRight,
			SuperFunction<X, RO> onException
	) {
		return either(
				Either::left,
				right -> {
					try {
						return right(onRight.apply(right));
					} catch (Exception e) {
						return right(onException.apply((X) e));
					}
				}
		);
	}

	@SuppressWarnings ("unchecked")
	public <LO, X extends Exception> Either<LO, TRight> tryFlatMapLeft (
			CheckedFunction<TLeft, Either<LO, TRight>, X> onLeft,
			SuperFunction<X, Either<LO, TRight>> onException
	) {
		return either(
				left -> {
					try {
						return onLeft.apply(left);
					} catch (Exception e) {
						return onException.apply((X) e);
					}
				}, Either::right
		);
	}

	@SuppressWarnings("unchecked")
	public <RO, X extends Exception> Either<TLeft, RO> tryFlatMapRight (
			CheckedFunction<TRight, Either<TLeft, RO>, X> onRight,
			SuperFunction<X, Either<TLeft, RO>> onException
	) {
		return either(
				Either::left,
				right -> {
					try {
						return onRight.apply(right);
					} catch (Exception e) {
						return onException.apply((X) e);
					}
				}
		);
	}

	public Either<TLeft, TRight> recoverToLeft(SuperFunction<TRight, TLeft> recovery) {
		return either(
				Either::left,
				right -> left(recovery.apply(right))
		);
	}

	public Either<TLeft, TRight> recoverToRight(SuperFunction<TLeft, TRight> recovery) {
		return either(
				left -> right(recovery.apply(left)),
				Either::right
		);
	}

	public TLeft getLeft () {
		return either(identity(), __ -> {throw new UnionTypeException(this);});
	}

	public TLeft getLeftOr (TLeft otherLeft) {
		return either(identity(), __ -> otherLeft);
	}

	public TLeft getLeftOrFrom (SuperSupplier<TLeft> otherLeftSupplier) {
		return either(identity(), __ -> otherLeftSupplier.get());
	}

	public TLeft getLeftOrRecover (SuperFunction<TRight, TLeft> mergingFunction) {
		return either(identity(), mergingFunction);
	}

	public TRight getRight () {
		return either(__ -> {throw new UnionTypeException(this);}, identity());
	}

	public TRight getRightOr (TRight otherRight) {
		return either(__ -> otherRight, identity());
	}

	public TRight getRightOrFrom (SuperSupplier<TRight> otherRightSupplier) {
		return either(__ -> otherRightSupplier.get(), identity());
	}

	public TRight getRightOrRecover (SuperFunction<TLeft, TRight> mergingFunction) {
		return either(mergingFunction, identity());
	}

	public Either<TLeft, TRight> onEitherPeek (SuperConsumer<TLeft> leftConsumer, SuperConsumer<TRight> rightConsumer) {
		return either(left -> left(peek(leftConsumer, left)), right -> right(peek(rightConsumer, right)));
	}

	public Either<TLeft, TRight> onLeftPeek (SuperConsumer<TLeft> leftConsumer) {
		return either(left -> left(peek(leftConsumer, left)), Either::right);
	}

	public Either<TLeft, TRight> onRightPeek (SuperConsumer<TRight> rightConsumer) {
		return either(Either::left, right -> right(peek(rightConsumer, right)));
	}

	public void onEitherDo (SuperConsumer<TLeft> leftConsumer, SuperConsumer<TRight> rightConsumer) {
		either(leftConsumer, rightConsumer);
	}

	public void onLeftDo (SuperConsumer<TLeft> leftConsumer) {
		either(leftConsumer, __ -> null);
	}

	public void onRightDo (SuperConsumer<TRight> rightConsumer) {
		either(__ -> null, rightConsumer);
	}

	public Either<TRight, TLeft> invert () {
		return either(Either::right, Either::left);
	}

	public Either<TLeft, TRight> filterLeft (
			Predicate<TLeft> predicate,
			SuperFunction<TLeft, TRight> onFalse
	) {
		return either(
				left -> predicate.test(left) ? left(left) : right(onFalse.apply(left)),
				Either::right
		);
	}

	public Either<TLeft, TRight> filterRight (
			Predicate<TRight> predicate,
			SuperFunction<TRight, TLeft> onFalse
	) {
		return either(
				Either::left,
				right -> predicate.test(right) ? right(right) : left(onFalse.apply(right))
		);
	}

	private static class Left<TLeft, TRight> extends Either<TLeft, TRight> {
		private final TLeft value;

		private Left (TLeft value) {
			this.value = value;
		}

		@Override
		public <T, T1 extends T, T2 extends T> T either (
				SuperFunction<TLeft, T1> onLeft,
				SuperFunction<TRight, T2> onRight
		) {
			return onLeft.apply(value);
		}

		@Override public String toString () {
			return String.format("Left(%s)", value);
		}

		@Override public boolean equals (Object o) {
			if (o == null || getClass() != o.getClass()) return false;
			Left<?, ?> left = (Left<?, ?>) o;
			return Objects.equals(value, left.value);
		}

		@Override public int hashCode () {
			return Objects.hashCode(value);
		}
	}

	private static class Right<TLeft, TRight> extends Either<TLeft, TRight> {
		private final TRight value;

		private Right (TRight value) {
			this.value = value;
		}

		@Override
		public <T, T1 extends T, T2 extends T> T either (
				SuperFunction<TLeft, T1> onLeft,
				SuperFunction<TRight, T2> onRight
		) {
			return onRight.apply(value);
		}

		@Override public String toString () {
			return String.format("Right(%s)", value);
		}

		@Override public boolean equals (Object o) {
			if (o == null || getClass() != o.getClass()) return false;
			Right<?, ?> right = (Right<?, ?>) o;
			return Objects.equals(value, right.value);
		}

		@Override public int hashCode () {
			return Objects.hashCode(value);
		}
	}
}
