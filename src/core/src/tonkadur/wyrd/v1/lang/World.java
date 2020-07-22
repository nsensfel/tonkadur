package tonkadur.wyrd.v1.lang;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import tonkadur.wyrd.v1.lang.Variable;
import tonkadur.wyrd.v1.lang.Sequence;

import tonkadur.wyrd.v1.lang.type.DictType;

import tonkadur.wyrd.v1.lang.meta.Instruction;

public class World
{
   protected final Set<String> required_extensions;

   protected final Map<String, Variable> variables;
   protected final Map<String, Sequence> sequences;
   protected final Map<String, DictType> dict_types;

   protected final List<Instruction> global_instructions;

   public World ()
   {
      required_extensions = new HashSet<String>();

      variables = new HashMap<String, Variable>();
      sequences = new HashMap<String, Sequence>();
      dict_types = new HashMap<String, DictType>();

      global_instructions = new ArrayList<Instruction>();
   }
}
