# Corpora

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