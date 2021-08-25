package tonkadur.fate.v1.lang.computation;

import tonkadur.parser.Origin;

import tonkadur.fate.v1.parser.ParserData;

import tonkadur.fate.v1.lang.type.Type;
import tonkadur.fate.v1.lang.type.FutureType;

import tonkadur.fate.v1.lang.meta.Computation;
import tonkadur.fate.v1.lang.meta.ComputationVisitor;
import tonkadur.fate.v1.lang.meta.VariableFromWord;

public class AmbiguousWord extends Computation
{
   /***************************************************************************/
   /**** MEMBERS **************************************************************/
   /***************************************************************************/
   protected final ParserData parser;
   protected final String as_string;
   protected Computation result;

   protected void assert_is_resolved ()
   {
      if (result == null)
      {
         System.err.println("[F] Ambiguous word " + toString() + " at:");
         System.err.println(get_origin().toString());

         System.exit(-1);
      }
   }

   /***************************************************************************/
   /**** PUBLIC ***************************************************************/
   /***************************************************************************/
   /**** Constructors *********************************************************/
   public AmbiguousWord
   (
      final ParserData parser,
      final Origin origin,
      final String as_string
   )
   {
      super(origin, new FutureType(origin));

      this.parser = parser;
      this.as_string = as_string;
      result = null;
   }

   /**** Accessors ************************************************************/
   @Override
   public void get_visited_by (final ComputationVisitor cv)
   throws Throwable
   {
      assert_is_resolved();

      result.get_visited_by(cv);
   }

   @Override
   public Type get_type ()
   {
      if (result == null)
      {
         return type;
      }

      return result.get_type();
   }

   @Override
   public void expect_non_string ()
   {
      try
      {
         result = VariableFromWord.generate(parser, get_origin(), as_string);
      }
      catch (final Throwable t)
      {
         t.printStackTrace();

         System.exit(-1);
      }

      ((FutureType) type).resolve_to(result.get_type());
   }

   @Override
   public void expect_string ()
   {
      result = Constant.build_string(get_origin(), as_string);

      ((FutureType) type).resolve_to(result.get_type());
   }

   public String get_value_as_string ()
   {
      return as_string;
   }

   @Override
   public void use_as_reference ()
   throws ParsingError
   {
      result.use_as_reference();
   }

   /**** Misc. ****************************************************************/
   @Override
   public String toString ()
   {
      final StringBuilder sb = new StringBuilder();

      sb.append("(AmbiguousWord \"");
      sb.append(as_string);
      sb.append("\"");

      if (result == null)
      {
         sb.append(" (unresolved))");
      }
      else
      {
         sb.append(" resolved to ");
         sb.append(result.toString());
         sb.append(")");
      }

      return sb.toString();
   }
}
