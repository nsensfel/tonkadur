package tonkadur.wyrd.v1.lang;

import java.util.List;

import tonkadur.wyrd.v1.lang.meta.Instruction;

public class Sequence
{
   protected final String name;
   protected final List<Instruction> content;

   public Sequence (final String name, final List<Instruction> content)
   {
      this.name = name;
      this.content = content;
   }

   public String get_name ()
   {
      return name;
   }

   public List<Instruction> get_content ()
   {
      return content;
   }
}
