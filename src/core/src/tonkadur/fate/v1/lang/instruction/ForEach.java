package tonkadur.fate.v1.lang.instruction;

import java.util.Collections;
import java.util.List;

import tonkadur.error.ErrorManager;

import tonkadur.parser.Origin;
import tonkadur.parser.ParsingError;

import tonkadur.fate.v1.lang.type.Type;

import tonkadur.fate.v1.lang.meta.InstructionVisitor;
import tonkadur.fate.v1.lang.meta.Instruction;
import tonkadur.fate.v1.lang.meta.Computation;
import tonkadur.fate.v1.lang.meta.RecurrentChecks;

public class ForEach extends Instruction
{
   /***************************************************************************/
   /**** MEMBERS **************************************************************/
   /***************************************************************************/
   protected final Computation collection;
   protected final String var_name;
   protected final List<Instruction> body;

   /***************************************************************************/
   /**** PROTECTED ************************************************************/
   /***************************************************************************/
   /**** Constructors *********************************************************/
   protected ForEach
   (
      final Origin origin,
      final Computation collection,
      final String var_name,
      final List<Instruction> body
   )
   {
      super(origin);

      this.collection = collection;
      this.var_name = var_name;
      this.body = body;
   }

   /***************************************************************************/
   /**** PUBLIC ***************************************************************/
   /***************************************************************************/
   /**** Constructors *********************************************************/
   public static ForEach build
   (
      final Origin origin,
      final Computation collection,
      final String var_name,
      final List<Instruction> body
   )
   throws ParsingError
   {
      collection.expect_non_string();

      return new ForEach(origin, collection, var_name, body);
   }

   /**** Accessors ************************************************************/
   @Override
   public void get_visited_by (final InstructionVisitor iv)
   throws Throwable
   {
      iv.visit_for_each(this);
   }

   public Computation get_collection ()
   {
      return collection;
   }

   public String get_parameter_name ()
   {
      return var_name;
   }

   public List<Instruction> get_body ()
   {
      return body;
   }

   /**** Misc. ****************************************************************/
   @Override
   public String toString ()
   {
      final StringBuilder sb = new StringBuilder();

      sb.append("(ForEach");
      sb.append(System.lineSeparator());
      sb.append(collection.toString());
      sb.append(System.lineSeparator());
      sb.append(var_name);
      sb.append(System.lineSeparator());
      sb.append(body.toString());

      sb.append(")");

      return sb.toString();
   }
}
