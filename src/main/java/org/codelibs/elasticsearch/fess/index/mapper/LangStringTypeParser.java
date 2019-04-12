package org.codelibs.elasticsearch.fess.index.mapper;

import static org.elasticsearch.index.mapper.TypeParsers.parseTextField;

import java.util.Iterator;
import java.util.Map;

import org.elasticsearch.common.xcontent.support.XContentMapValues;
import org.elasticsearch.index.mapper.DocumentMapperParser;
import org.elasticsearch.index.mapper.Mapper;
import org.elasticsearch.index.mapper.MapperParsingException;
import org.elasticsearch.index.mapper.TextFieldMapper;
import org.elasticsearch.index.mapper.TextFieldMapper.Defaults;

// from org.elasticsearch.index.mapper.TextFieldMapper.TypeParser
public class LangStringTypeParser implements Mapper.TypeParser {

    public static final String CONTENT_TYPE = "langstring";

    private static final String SEPARATOR_SETTING_KEY = "separator";

    private static final String LANG_SETTING_KEY = "lang";

    private static final String LANG_FIELD_SETTING_KEY = "lang_field";

    private static final String LANG_BASE_NAME_SETTING_KEY = "lang_base_name";

    @Override
    public Mapper.Builder parse(String fieldName, Map<String, Object> node, ParserContext parserContext) throws MapperParsingException {
        TextFieldMapper.Builder builder = new TextFieldMapper.Builder(fieldName);
        builder.fieldType().setIndexAnalyzer(parserContext.getIndexAnalyzers().getDefaultIndexAnalyzer());
        builder.fieldType().setSearchAnalyzer(parserContext.getIndexAnalyzers().getDefaultSearchAnalyzer());
        builder.fieldType().setSearchQuoteAnalyzer(parserContext.getIndexAnalyzers().getDefaultSearchQuoteAnalyzer());
        parseTextField(builder, fieldName, node, parserContext);
        for (Iterator<Map.Entry<String, Object>> iterator = node.entrySet().iterator(); iterator.hasNext();) {
            Map.Entry<String, Object> entry = iterator.next();
            String propName = entry.getKey();
            Object propNode = entry.getValue();
            if (propName.equals("position_increment_gap")) {
                int newPositionIncrementGap = XContentMapValues.nodeIntegerValue(propNode, -1);
                builder.positionIncrementGap(newPositionIncrementGap);
                iterator.remove();
            } else if (propName.equals("fielddata")) {
                builder.fielddata(XContentMapValues.nodeBooleanValue(propNode, "fielddata"));
                iterator.remove();
            } else if (propName.equals("eager_global_ordinals")) {
                builder.eagerGlobalOrdinals(XContentMapValues.nodeBooleanValue(propNode, "eager_global_ordinals"));
                iterator.remove();
            } else if (propName.equals("fielddata_frequency_filter")) {
                Map<?, ?> frequencyFilter = (Map<?, ?>) propNode;
                double minFrequency = XContentMapValues.nodeDoubleValue(frequencyFilter.remove("min"), 0);
                double maxFrequency = XContentMapValues.nodeDoubleValue(frequencyFilter.remove("max"), Integer.MAX_VALUE);
                int minSegmentSize = XContentMapValues.nodeIntegerValue(frequencyFilter.remove("min_segment_size"), 0);
                builder.fielddataFrequencyFilter(minFrequency, maxFrequency, minSegmentSize);
                DocumentMapperParser.checkNoRemainingFields(propName, frequencyFilter, parserContext.indexVersionCreated());
                iterator.remove();
            } else if (propName.equals("index_prefixes")) {
                Map<?, ?> indexPrefix = (Map<?, ?>) propNode;
                int minChars = XContentMapValues.nodeIntegerValue(indexPrefix.remove("min_chars"), Defaults.INDEX_PREFIX_MIN_CHARS);
                int maxChars = XContentMapValues.nodeIntegerValue(indexPrefix.remove("max_chars"), Defaults.INDEX_PREFIX_MAX_CHARS);
                builder.indexPrefixes(minChars, maxChars);
                DocumentMapperParser.checkNoRemainingFields(propName, indexPrefix, parserContext.indexVersionCreated());
                iterator.remove();
            } else if (propName.equals("index_phrases")) {
                builder.indexPhrases(XContentMapValues.nodeBooleanValue(propNode, "index_phrases"));
                iterator.remove();
            } else if (propName.equals(SEPARATOR_SETTING_KEY)) {
                iterator.remove();
            } else if (propName.equals(LANG_SETTING_KEY)) {
                iterator.remove();
            } else if (propName.equals(LANG_FIELD_SETTING_KEY)) {
                iterator.remove();
            } else if (propName.equals(LANG_BASE_NAME_SETTING_KEY)) {
                iterator.remove();
            }
        }
        return builder;
    }
}
