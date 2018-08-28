package uk.co.bconline.ndelius.analyzer;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.LowerCaseFilter;
import org.apache.lucene.analysis.core.WhitespaceTokenizer;

import lombok.val;

public final class CaseInsensitiveWhitespaceAnalyzer extends Analyzer
{
	@Override
	protected TokenStreamComponents createComponents(String fieldName)
	{
		val tokenizer = new WhitespaceTokenizer();
		return new TokenStreamComponents(tokenizer, new LowerCaseFilter(tokenizer));
	}
}
