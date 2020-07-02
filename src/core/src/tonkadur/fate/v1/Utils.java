package tonkadur.fate.v1;

import java.io.IOException;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;

import tonkadur.fate.v1.parser.FateLexer;
import tonkadur.fate.v1.parser.FateParser;

import tonkadur.fate.v1.lang.World;

public class Utils
{
   /* Utility class. */
   private Utils () {}

   public static void add_file_content
   (
      final String filename,
      final World world
   )
   throws IOException
   {
      final CommonTokenStream tokens;
      final FateLexer lexer;
      final FateParser parser;

      lexer = new FateLexer(CharStreams.fromFileName(filename));
      tokens = new CommonTokenStream(lexer);
      parser = new FateParser(tokens);
      parser.fate_file(world);
   }
}
