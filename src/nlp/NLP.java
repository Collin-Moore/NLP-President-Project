package nlp;

import edu.stanford.nlp.util.Timing;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

import edu.stanford.nlp.dcoref.CorefChain;
import edu.stanford.nlp.dcoref.CorefCoreAnnotations.CorefChainAnnotation;
import edu.stanford.nlp.ie.util.RelationTriple;
import edu.stanford.nlp.ling.*;
import edu.stanford.nlp.ling.CoreAnnotations.*;
import edu.stanford.nlp.naturalli.ForwardEntailer;
import edu.stanford.nlp.naturalli.ForwardEntailerSearchProblem;
import edu.stanford.nlp.naturalli.NaturalLogicAnnotations;
import edu.stanford.nlp.naturalli.NaturalLogicWeights;
import edu.stanford.nlp.pipeline.*;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations.CollapsedCCProcessedDependenciesAnnotation;
import edu.stanford.nlp.semgraph.SemanticGraphEdge;
import edu.stanford.nlp.trees.GrammaticalRelation;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreeCoreAnnotations.TreeAnnotation;
import edu.stanford.nlp.util.CoreMap;
import edu.stanford.nlp.util.Pair;


public class NLP {
    private static final String OUTPUT_PATH = "./res/processed.csv";
    private static final String DELIMITER = "|";

    /**
     * @param args
     */
    public static void main(String[] args) {
        String annotators = "tokenize, ssplit, pos, lemma, depparse, natlog, openie";
        Properties props = new Properties();
        props.put("annotators", annotators);
        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);

        processFile("./res/test.txt", OUTPUT_PATH, pipeline);
        SearchTriples search = loadSearchTriples(OUTPUT_PATH);

        Scanner input = new Scanner(System.in);
        while(true) {
            System.out.println("Enter a sentence to be verified, or enter exit() to exit: ");
            String query = input.nextLine();
            if (query.equals("exit()")) {
                System.out.println("Exiting...");
                break;
            }
            if (searchForQuery(query, search, pipeline)) {
                System.out.println("According to our calculations, that is a true statement");
            } else {
                System.out.println("That statement is false");
            }
        }



    }

    private static void processFile(String file, String outPath, StanfordCoreNLP pipeline) {
        try {
            File tmpdir = new File(outPath);
            if (tmpdir.exists()) {
                System.err.println("The output file has already been created");
                return;
            }
            BufferedWriter out = new BufferedWriter(new FileWriter(outPath, true));
            Scanner scanner = new Scanner(new File(file));
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String result = processLine(line, pipeline);
                out.write(result);
                out.newLine();
            }
            scanner.close();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String processLine(String line, StanfordCoreNLP pipeline) {
        StringBuilder sb = new StringBuilder();
        Annotation text = new Annotation(line);
        pipeline.annotate(text);
        List<CoreMap> sentences = text.get(SentencesAnnotation.class);
        if (sentences.isEmpty()) {
            System.err.println("No sentence found!");
            return " ";
        }
        CoreMap sentence = sentences.get(0);
        Collection<RelationTriple> triples = sentence.get(NaturalLogicAnnotations.RelationTriplesAnnotation.class);
        if (triples != null && !triples.isEmpty()) {
            for (RelationTriple t : triples) {
                sb.append(t.subjectLemmaGloss());
                sb.append(DELIMITER);
                sb.append(t.relationLemmaGloss());
                sb.append(DELIMITER);
                sb.append(t.objectLemmaGloss());
                sb.append(DELIMITER);
            }
            return sb.substring(0, sb.length() - 1);
        }
        return " ";

    }

    private static SearchTriples loadSearchTriples(String filePath) {
        SearchTriples searchObj = new SearchTriples();
        try {
            Scanner scanner = new Scanner(new File(filePath));
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                line = line + DELIMITER + ".";
                String[] triples = line.split(DELIMITER);
                searchObj.insertTriples(triples);
            }
            scanner.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return searchObj;
    }

    private static boolean searchForQuery(String query, SearchTriples search, StanfordCoreNLP pipeline) {
        String processedQuery = processLine(query,pipeline)+DELIMITER+".";
        String[] triple = processedQuery.split(DELIMITER);
        return search.exists(triple);
    }
}


    //        props.put("annotators", "tokenize, ssplit, pos, lemma, ner, parse, depparse, natlog, openie");
//        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
// read some text in the text variable
//        String text = "All dogs run.";
//        String text = "Pick up the blue block.";
//        String text = "In 1921, Einstein received the Nobel Prize for his original work on the photoelectric effect.";
//        String text = "Did Einstein receive the Nobel Prize?";
//        String text = "Mary saw a ring through the window and asked John for it.";
// create an empty Annotation just with the given text
//        Annotation document = new Annotation(text);

// run all Annotators on this text
//        pipeline.annotate(document);

// these are all the sentences in this document
// a CoreMap is essentially a Map that uses class objects as keys and has values with custom types
//        List<CoreMap> sentences = document.get(SentencesAnnotation.class);


//        for(CoreMap sentence: sentences) {
// traversing the words in the current sentence
// a CoreLabel is a CoreMap with additional token-specific methods
//          for (CoreLabel token: sentence.get(TokensAnnotation.class)) {
//            // this is the text of the token
//            String word = token.get(TextAnnotation.class);
//            // this is the POS tag of the token
//            String pos = token.get(PartOfSpeechAnnotation.class);
//            // this is the NER label of the token
//            String ne = token.get(NamedEntityTagAnnotation.class);
//            System.out.println("word " + word + " ,pos: " + pos + " ,ne: " + ne);
//          }

//            Collection<RelationTriple> triples = sentence.get(NaturalLogicAnnotations.RelationTriplesAnnotation.class);
//            for (RelationTriple t : triples) {
//                System.out.println(t.subjectLemmaGloss() + "\t" + t.relationLemmaGloss() + "\t" + t.objectLemmaGloss());
//            }

//          // this is the parse tree of the current sentence
//          Tree tree = sentence.get(TreeAnnotation.class);
//          System.out.println();
//          System.out.println(tree);

//          // this is the Stanford dependency graph of the current sentence

//          SemanticGraph tree = sentence.get(SemanticGraphCoreAnnotations.BasicDependenciesAnnotation.class);
//          if (tree == null) {
//            tree = sentence.get(SemanticGraphCoreAnnotations.EnhancedDependenciesAnnotation.class);
//          }

//          NaturalLogicAnnotator annotator = new NaturalLogicAnnotator();
//          annotator.annotatePolarity(CoreMap sentence);
//
//          SemanticGraph dependencies = sentence.get(CollapsedCCProcessedDependenciesAnnotation.class);
//          System.out.println("here");
//          System.out.println(dependencies);
//
//          NaturalLogicWeights weights = new NaturalLogicWeights();
//
//          ForwardEntailer fe = new ForwardEntailer(5, 10, weights);
//
//          ForwardEntailerSearchProblem p = fe.apply(dependencies, true);

//          IndexedWord predicate = dependencies.getFirstRoot();
//          IndexedWord subject = null;
//          String quantifier = null;
//          Boolean goodToGo = false;
//          Boolean TopLevelNegation = false;
//          Boolean PredicateLevelNegation = false;
//          List<Pair<GrammaticalRelation,IndexedWord>> s = dependencies.childPairs(predicate);
//          for (Pair<GrammaticalRelation,IndexedWord> item : s) {
//        	  if (item.first.toString().equals("nsubj")) {
//        		  subject = item.second;
//        	  }
//        	  if (item.first.toString().equals("cop") && (item.second.originalText().toLowerCase().equals("are") || item.second.originalText().toLowerCase().equals("is"))) {
//        		  goodToGo = true;
//        	  }
//        	  if (item.first.toString().equals("neg")) {
//        		  PredicateLevelNegation = true;
//        	  }
//          }
//
//          List<Pair<GrammaticalRelation,IndexedWord>> t = dependencies.childPairs(subject);
//          for (Pair<GrammaticalRelation,IndexedWord> item : t) {
//        	  if (item.first.toString().equals("det")) {
//        		  quantifier = item.second.lemma().toString();
//        	  }
//        	  if (item.first.toString().equals("neg")) {
//        		  TopLevelNegation = true;
//        	  }
//          }
//
//          if (goodToGo) {
//          String output = "";
//          if (TopLevelNegation) output += "not ";
//          if (quantifier != null) output += quantifier + "(x): " + subject.lemma().toString() + "(x) ^ ";
//          if (PredicateLevelNegation) output += "not(";
//          if (quantifier == null) {
//        	  output += predicate.originalText() + "(" + subject.lemma().toString() + ")";
//          } else {
//        	  output += predicate.originalText() + "(x)";
//          }
//          if (PredicateLevelNegation) output += ")";
//
//          System.out.println(output);
//          } else {
//        	  System.out.println("Could not translate sentence.");
//          }

//        }

// This is the coreference link graph
// Each chain stores a set of mentions that link to each other,
// along with a method for getting the most representative mention
// Both sentence and token offsets start at 1!
//        Map<Integer, CorefChain> graph = document.get(CorefChainAnnotation.class);
//        System.out.println();
//        System.out.println(graph);