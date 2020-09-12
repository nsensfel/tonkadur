package tonkadur.fate.v1.lang.computation;

import tonkadur.parser.Origin;

import tonkadur.error.ErrorManager;

import tonkadur.fate.v1.lang.type.Type;
import tonkadur.fate.v1.lang.type.LambdaType;
import tonkadur.fate.v1.lang.type.CollectionType;

import tonkadur.fate.v1.lang.instruction.IndexedMap;

import tonkadur.fate.v1.lang.meta.ComputationVisitor;
import tonkadur.fate.v1.lang.meta.Computation;
import tonkadur.fate.v1.lang.meta.Reference;

public class IndexedMapComputation extends Computation
{
   /***************************************************************************/
   /**** MEMBERS **************************************************************/
   /***************************************************************************/
   protected final IndexedMap instruction;

   /***************************************************************************/
   /**** PROTECTED ************************************************************/
   /***************************************************************************/
   /**** Constructors *********************************************************/
   protected IndexedMapComputation
   (
      final IndexedMap instruction,
      final Type output_type
   )
   {
      super(instruction.get_origin(), output_type);

      this.instruction = instruction;
   }

   /***************************************************************************/
   /**** PUBLIC ***************************************************************/
   /***************************************************************************/
   /**** Constructors *********************************************************/
   public static IndexedMapComputation build
   (
      final Origin origin,
      final Computation lambda_function,
      final Reference collection_in
   )
   throws Throwable
   {
      final Type type;
      final IndexedMap parent;

      parent = IndexedMap.build(origin, lambda_function, collection_in, null);

      type =
         CollectionType.build
         (
            origin,
            ((LambdaType) lambda_function.get_type()).get_return_type(),
            ((CollectionType) collection_in.get_type()).is_set(),
            "auto generated"
         );

      return new IndexedMapComputation(parent, type);
   }

   /**** Accessors ************************************************************/
   @Override
   public void get_visited_by (final ComputationVisitor cv)
   throws Throwable
   {
      cv.visit_indexed_map(this);
   }

   public IndexedMap get_instruction ()
   {
      return instruction;
   }

   /**** Misc. ****************************************************************/
   @Override
   public String toString ()
   {
      final StringBuilder sb = new StringBuilder();

      sb.append("(ComputationOf ");
      sb.append(instruction.toString());

      sb.append(")");

      return sb.toString();
   }
}
