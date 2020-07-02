package tonkadur.parser;

import java.io.IOException;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;

public class Fate
{
   /* Utility class. */
   private Fate () {}

   public static void parse_file (final String filename)
   throws IOException
   {
      final CommonTokenStream tokens;
      final LangLexer lexer;
      final LangParser parser;

      lexer = new LangLexer(CharStreams.fromFileName(filename));
      tokens = new CommonTokenStream(lexer);
      parser = new LangParser(tokens);
      parser.fate_file();
   }
}
