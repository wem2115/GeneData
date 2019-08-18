# GeneData
I opted to use RandomAccessFiles for this project as they allow
for fast, random access to data in persisted memory. The input file ‘probes.txt’ is
parsed by the program, and split into a file for coordinates, and a file for stored
values. The program also keeps track of the file byte locations of each chromosome,
so that this portion of the file can be quickly accessed for whatever chromosome is
being queried. This method allows for extremely fast search and data access, even
on such a large file.
There is little error handling in this project, as I was only able to work on it for a
limited amount of time each day. Therefore, improperly formatted queries will
simply fail (namely, queries not in one of the two forms given in the prompt).
On first running the program, ensure that probes.txt is in the same folder as the
JavaIO.java file, wherever that may be on your local system. The program will then
build the RAFs and queries can be made from there. It can be run from the terminal
or the Eclipse IDE, which is what I used to develop it.
Simply re-run the program for each new query, it will know not to re-build the files
after they are already present in the classpath.
