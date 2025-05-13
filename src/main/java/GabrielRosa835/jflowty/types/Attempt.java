package GabrielRosa835.jflowty.types;

import java.util.*;
import java.util.function.*;

public abstract class Attempt<T, X> extends Group<T, X> {

	protected Attempt() {}

	public boolean isSuccess() {
		return this instanceof Attempt.Success;
	}
	public boolean isFailure() {
		return this instanceof Attempt.Failure;
	}

	public static <T, X extends Exception> Attempt<T, X> success(T success) {
		return new Attempt.Success<>(success);
	}

	public static <T, X extends Exception> Attempt<T, X> failure(X failure) {
		return new Attempt.Failure<>(failure);
	}

	public static <T, X extends Exception> Attempt<T, X> failure(X failure, Class<T> successType) {
		return new Attempt.Failure<>(failure);
	}

	private static class Success<T, X> extends Attempt<T, X> {
		private final T value;

		private Success(T value) {
			this.value = value;
		}

		@Override
		public <U, U1 extends U, U2 extends U> U either(Function<T, U1> onSuccess, Function<X, U2> __) {
			return onSuccess.apply(value);
		}

		public String toString() {
			return "Success[" + value + ']';
		}

		public boolean equals(Object o) {
			if (this == o) {
				return true;
			} else if (o != null && this.getClass() == o.getClass()) {
				Attempt.Success<?, ?> that = (Attempt.Success) o;
				return Objects.equals(value, that.value);
			} else {
				return false;
			}
		}

		public int hashCode() {
			return Objects.hash(value);
		}
	}

	private static class Failure<T, X> extends Attempt<T, X> {
		private final X error;

		private Failure(X error) {
			this.error = error;
		}

		@Override
		public <U, U1 extends U, U2 extends U> U either(Function<T, U1> __, Function<X, U2> onFailure) {
			return onFailure.apply(error);
		}

		public String toString() {
			return "Failure[" + error + ']';
		}

		public boolean equals(Object o) {
			if (this == o) {
				return true;
			} else if (o != null && this.getClass() == o.getClass()) {
				Attempt.Failure<?, ?> that = (Attempt.Failure) o;
				return Objects.equals(error, that.error);
			} else {
				return false;
			}
		}

		public int hashCode() {
			return Objects.hash(error);
		}
	}
}
