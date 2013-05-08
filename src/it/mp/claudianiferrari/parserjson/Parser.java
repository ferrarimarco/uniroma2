package it.mp.claudianiferrari.parserjson;


public interface Parser<T> {

	T parse(String url);
}
