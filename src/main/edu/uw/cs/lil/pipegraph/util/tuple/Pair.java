package edu.uw.cs.lil.pipegraph.util.tuple;

import java.io.Serializable;
import java.util.Objects;

public class Pair<A, B> implements Serializable {
	private final A	first;
	private final B	second;

	private Pair(A first, B second) {
		this.first = first;
		this.second = second;
	}

	public static <A, B> Pair<A, B> of(A first, B second) {
		return new Pair<>(first, second);
	}

	@Override
	public boolean equals(Object other) {
		if (other instanceof Pair) {
			return Objects.equals(this.first, ((Pair<?, ?>) other).first)
					&& Objects.equals(this.second, ((Pair<?, ?>) other).second);
		} else {
			return false;
		}
	}

	public A first() {
		return first;
	}

	@Override
	public int hashCode() {
		return Objects.hash(first, second);
	}

	public B second() {
		return second;
	}

	@Override
	public String toString() {
		return "(" + first + "," + second + ")";
	}

}