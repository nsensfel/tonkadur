package tonkadur.wyrd.v1.lang.meta;

public abstract class Instruction
{
   public void get_visited_by (final InstructionVisitor iv)
   throws Throwable
   {
      throw new Exception("Unhandled Wyrd AST Instruction Node.");
   }
}
