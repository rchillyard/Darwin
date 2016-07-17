# Darwin
Darwin framework (Scala) for Genetic Algorithms (based on Darwin at SourceForge).

This is a general purpose framework for performing evolutionary computation (via so-called genetic algorithms).
The types defined in the framework are as generic as possible, thus allowing the
programmer to extend these types for their own purposes.
Generally speaking, these types which may be extended are in the form of traits.

Nevertheless, in contrast to the extensibility of the types, there is a certain structure which is normally followed to
 ensure that evolution proceeds as expected.
 This basic structure follows the natural world as much as is reasonably possible.
 Of course, if you don't want this full structure, you aren't obliged to utilize all of it.


The full progression begins with a _Sequence[B]_, consisting of a _Seq_ of bases, each of parametric type _B_ (defined by the extender).
A _Sequence_ is transcribed into _Genotype[P,G]_ via a _Transcriber[B,G]_.

A _Genotype_ is made of a _Seq_ of _Gene[P,G]_ where _P_ and _G_ are parametric types which relate to ploidy and genetics, respectively.
A _Genotype_ is expressed as a _Phenotype[T]_ via an _Expresser[P,G,T]_.

A _Phenotype_ is made of a _Seq_ of _Trait[T]_ where _T_ is a parametric type related to observable characteristics (traits).
A _Phenotype_ is adapted to an _Adaptatype[X]_ via an _Adapter[T,X]_.

A _Adaptatype_ consists of a _Seq_ of _Adaptation[X]_ where _X_ is a parametric type related to fitness of a trait in an environment.



