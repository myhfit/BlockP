package bp.parser;

public interface BPParser<S, R>
{
	R parse(S source);
}
