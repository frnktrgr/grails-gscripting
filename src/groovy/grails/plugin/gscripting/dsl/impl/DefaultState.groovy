package grails.plugin.gscripting.dsl.impl


class DefaultState extends LinkedHashMap {

	public DefaultState() {
		super();
	}

	public DefaultState(int initialCapacity, float loadFactor, boolean accessOrder) {
		super(initialCapacity, loadFactor, accessOrder);
	}

	public DefaultState(int initialCapacity, float loadFactor) {
		super(initialCapacity, loadFactor);
	}

	public DefaultState(int initialCapacity) {
		super(initialCapacity);
	}

	public DefaultState(Map m) {
		super(m);
	}

}
