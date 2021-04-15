package domain.common

interface DependencyResolver {
    /**
     * @param className - name of class which dependencies we want to get
     * @return list of dependency names for class className
     */
    fun getDependencyNames(className: String) : List<String>
}
