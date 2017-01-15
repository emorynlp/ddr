## DDG Conversion

DDG conversion generates deep dependency graphs from the Penn Treebank style constituency trees.

* Download the conversion script: [nlp4j-ddr.jar](http://nlp.mathcs.emory.edu/nlp4j/nlp4j-ddr.jar).
* Make sure [Java 8 or above](http://www.oracle.com/technetwork/java/javase/downloads) is installed on your machine:

   ```
$ java -version
java version "1.8.x"
Java(TM) SE Runtime Environment (build 1.8.x)
...
   ```

* Run the following command:

   ```
java -cp nlp4j-ddr.jar edu.emory.mathcs.nlp.bin.DDGConvert -i <filepath> [ -r -n -pe <string> -oe <string>]
   ```
   
   * `-i`: the path to the parse file or a directory containing the parse files to convert.
   * `-r`: if set, process all files with the extension in the subdirectories of the input directory recursively.
   * `-n`: if set, normalize the parse trees before the conversion.
   * `-pe`: the extension of the parse files; required if the input path indicates a directory (default: `parse`).
   * `-oe`: the extension of the output files (default: `ddg`).

## Corpora

DDG conversion has been tested on the following corpora. Some of these corpora require you to be a member of the [Linguistic Data Consortium](https://www.ldc.upenn.edu) (LDC). Retrieve the corpora from LDC and run the following command for each corpus to generate DDG.

* [OntoNotes Release 5.0](https://catalog.ldc.upenn.edu/LDC2013T19):

   ```
java -cp nlp4j-ddr.jar edu.emory.mathcs.nlp.bin.DDGConvert -r -i ontonotes-release-5.0/data/files/data/english/annotations
   ```

* [English Web Treebank](https://catalog.ldc.upenn.edu/LDC2012T13):

   ```
java -cp nlp4j-ddr.jar edu.emory.mathcs.nlp.bin.DDGConvert -r -i eng_web_tbk/data -pe tree
   ```

* [QuestionBank with Manually Revised Treebank Annotation 1.0](https://catalog.ldc.upenn.edu/LDC2012R121):

   ```
java -cp nlp4j-ddr.jar edu.emory.mathcs.nlp.bin.DDGConvert -i QB-revised.tree
   ```

## Merge

We have internally updated these corpora to reduce annotation errors and produce a richer representation. If you want to take advantage of our latest updates, merge the original annotation with our annotation. You still need to retrieve the original corpora from LDC.

* Clone this repository:

   ```
git clone https://github.com/emorynlp/ddr.git
   ```

* Run the following command:

   ```
java -cp nlp4j-ddr.jar edu.emory.mathcs.nlp.bin.DDGMerge <source path> <target path> <parse ext>
   ```

   * `<source path>`: the path to the original corpus.
   * `<target path>`: the path to our annotation.
   * `<parse ext`>: the extension of the parse files.


* [OntoNotes Release 5.0](https://catalog.ldc.upenn.edu/LDC2013T19):

   ```
java -cp nlp4j-ddr.jar edu.emory.mathcs.nlp.bin.DDGMerge ontonotes-release-5.0/data/files/data/english/annotations ddr/english/ontonotes parse
   ```

* [English Web Treebank](https://catalog.ldc.upenn.edu/LDC2012T13):

   ```
java -cp nlp4j-ddr.jar edu.emory.mathcs.nlp.bin.DDGMerge eng_web_tbk/data ddr/english/google/ewt tree
   ```

* [QuestionBank with Manually Revised Treebank Annotation 1.0](https://catalog.ldc.upenn.edu/LDC2012R121):

   ```
java -cp nlp4j-ddr.jar edu.emory.mathcs.nlp.bin.DDGMerge QB-revised.tree ddr/english/google/qb/QB-revised.tree.skel tree
   ```
