package com.bc.util;

import java.io.Reader;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.LetterTokenizer;
import org.apache.lucene.analysis.LowerCaseFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.snowball.SnowballFilter;
import org.apache.lucene.analysis.standard.StandardFilter;

public class EnglishAnalyzer extends Analyzer {

    @Override
    public TokenStream tokenStream(String name, Reader reader) {
        TokenStream result = new LetterTokenizer(reader);
        result = new StandardFilter(result);
        result = new LowerCaseFilter(result);
        //result = new NGramTokenFilter(result, 3, 15);
        result = new SnowballFilter(result, "English");
        return result;
    }

}
