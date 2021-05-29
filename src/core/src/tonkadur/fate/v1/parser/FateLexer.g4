lexer grammar FateLexer;

@header
{
   package tonkadur.fate.v1.parser;
}

fragment SEP: [ \t\r\n]+;
fragment US: '_'?;

fragment DICT_NS: 'dict:';
fragment LIST_NS: 'list:';
fragment SET_NS: 'set:';
fragment STRUCT_NS: 'struct:';

WS: SEP;

L_PAREN: '(';
R_PAREN: ')';




FATE_VERSION_KW: L_PAREN 'fate'US'version' SEP+;




DECLARE_ALIAS_TYPE_KW:
   L_PAREN
      ((('declare'|('def''ine'?))US(('sub'|'alias')US)?'type')|'typedef')
   SEP+;
DECLARE_STRUCT_TYPE_KW:
   L_PAREN ('declare'|('def''ine'?))US('struct''ure'?)(US'type')? SEP+;
DECLARE_EXTRA_INSTRUCTION_KW:
   L_PAREN ('declare'|('def''ine'?))US'extra'US'instruction' SEP+;
DECLARE_EXTRA_COMPUTATION_KW:
   L_PAREN ('declare'|('def''ine'?))US'extra'US'computation' SEP+;
DECLARE_EXTRA_TYPE_KW:
   L_PAREN ('declare'|('def''ine'?))US'extra'US'type' SEP+;
DECLARE_EVENT_TYPE_KW:
   L_PAREN ('declare'|('def''ine'?))(US'input')?US'event'(US'type')? SEP+;
DECLARE_TEXT_EFFECT_KW:
   L_PAREN ('declare'|('def''ine'?))US'text'US'effect' SEP+;
DECLARE_GLOBAL_VARIABLE_KW: L_PAREN 'global' SEP+;
DECLARE_EXTERNAL_VARIABLE_KW: L_PAREN 'extern'('al'?) SEP+;

DEFINE_SEQUENCE_KW:
   L_PAREN
      ('declare'|('def''ine'?))US(('seq'('uence'?))|('proc'('edure'?)))
   SEP+;

IMP_IGNORE_ERROR_KW: L_PAREN 'ignore'US('error'|'warning')'!' SEP+;
REQUIRE_EXTENSION_KW: L_PAREN 'require'US'extension' SEP+;
REQUIRE_KW: L_PAREN 'require' SEP+;
INCLUDE_KW: L_PAREN 'include' SEP+;




DECLARE_LOCAL_VARIABLE_KW: L_PAREN 'local' SEP+;




ABS_KW: L_PAREN 'abs'('olute'?) SEP+;
CLAMP_KW: L_PAREN ('clamp') SEP+;
DIVIDE_KW: L_PAREN (('div''ide'?)|'/') SEP+;
MAX_KW: L_PAREN ('max'('imum'?)) SEP+;
MINUS_KW: L_PAREN ('minus'|'-') SEP+;
MIN_KW: L_PAREN ('min'('imum'?)) SEP+;
MODULO_KW: L_PAREN (('mod''ulo'?)|'%') SEP+;
PLUS_KW: L_PAREN ('plus'|'+') SEP+;
POWER_KW: L_PAREN (('pow''er'?)|'^'|'**'|) SEP+;
RANDOM_KW: L_PAREN (('rand''om'?)|'rnd') SEP+;
TIMES_KW: L_PAREN ('times'|'*') SEP+;




DICT_KW: L_PAREN 'dict'('ionary'?) SEP+;
DICT_FILTER_KW: L_PAREN DICT_NS 'filter' SEP+;
DICT_FOLD_KW: L_PAREN DICT_NS 'fold' SEP+;
DICT_FROM_LIST_KW:
   L_PAREN ((DICT_NS'from'US'list')|(LIST_NS'to'US'dict'('ionary'?))) SEP+;
DICT_GET_KW: L_PAREN DICT_NS 'get' SEP+;
DICT_HAS_KW: L_PAREN DICT_NS 'has' SEP+;
DICT_KEYS_KW: L_PAREN DICT_NS ('get'US)?'keys' SEP+;
DICT_MAP_KW: L_PAREN DICT_NS 'map' SEP+;
DICT_MERGE_KW: L_PAREN DICT_NS 'merge' SEP+;
DICT_REMOVE_KW: L_PAREN DICT_NS ('rm'|'remove'|'del'|'delete') SEP+;
DICT_SET_KW: L_PAREN DICT_NS 'set' SEP+;
DICT_SIZE_KW: L_PAREN DICT_NS 'size' SEP+;
DICT_TO_LIST_KW:
   L_PAREN ((DICT_NS'to'US'list')|(LIST_NS'from'US'dict'('ionary'?))) SEP+;
DICT_VALUES_KW: L_PAREN DICT_NS ('get'US)?'values' SEP+;
IMP_DICT_FILTER_KW: L_PAREN DICT_NS 'filter''!' SEP+;
IMP_DICT_FOLD_KW: L_PAREN DICT_NS 'fold''!' SEP+;
IMP_DICT_FROM_LIST_KW:
   L_PAREN ((DICT_NS'from'US'list')|(LIST_NS'to'US'dict'('ionary'?)))'!' SEP+;
IMP_DICT_GET_KW: L_PAREN DICT_NS 'get''!' SEP+;
IMP_DICT_HAS_KW: L_PAREN DICT_NS 'has''!' SEP+;
IMP_DICT_KEYS_KW: L_PAREN DICT_NS ('get'US)?'keys''!' SEP+;
IMP_DICT_MAP_KW: L_PAREN DICT_NS 'map''!' SEP+;
IMP_DICT_MERGE_KW: L_PAREN DICT_NS 'merge''!' SEP+;
IMP_DICT_REMOVE_KW: L_PAREN DICT_NS ('rm'|'remove'|'del'|'delete')'!' SEP+;
IMP_DICT_SET_KW: L_PAREN DICT_NS 'set''!' SEP+;
IMP_DICT_TO_LIST_KW:
   L_PAREN ((DICT_NS'to'US'list')|(LIST_NS'from'US'dict'('ionary'?)))'!' SEP+;
IMP_DICT_VALUES_KW: L_PAREN DICT_NS ('get'US)?'values''!' SEP+;




LIST_KW: L_PAREN 'list' SEP+;
LIST_ADD_ALL_KW: L_PAREN LIST_NS 'add'US'all'(US'elements')? SEP+;
LIST_ADD_ALL_KW: L_PAREN LIST_NS 'add'US'all'(US'elements')? SEP+;
LIST_ADD_AT_KW: L_PAREN LIST_NS 'add'(US'element')?US'at' SEP+;
LIST_ADD_KW: L_PAREN LIST_NS 'add'(US'element')? SEP+;
LIST_COUNT_KW: L_PAREN LIST_NS 'count' SEP+;
LIST_FILTER_KW: L_PAREN LIST_NS 'filter' SEP+;
LIST_FOLDL_KW: L_PAREN LIST_NS 'foldl' SEP+;
LIST_FOLDR_KW: L_PAREN LIST_NS 'foldr' SEP+;
LIST_GET_KW: L_PAREN LIST_NS 'get' SEP+;
LIST_INDEXED_FILTER_KW: L_PAREN LIST_NS 'indexed'US'filter' SEP+;
LIST_INDEXED_MAP_KW: L_PAREN LIST_NS 'indexed'US'map' SEP+;
LIST_INDEXED_MERGE_KW: L_PAREN LIST_NS 'indexed'US'merge' SEP+;
LIST_INDEXED_PARTITION_KW: L_PAREN LIST_NS 'indexed'US'partition' SEP+;
LIST_INDEX_OF_KW: L_PAREN LIST_NS 'index'US'of' SEP+;
LIST_IS_EMPTY_KW: L_PAREN LIST_NS 'is'US'empty' SEP+;
LIST_IS_MEMBER_KW: L_PAREN LIST_NS (('is'US'member')|'contains'|'has') SEP+;
LIST_MAP_KW: L_PAREN LIST_NS 'map' SEP+;
LIST_MERGE_KW: L_PAREN LIST_NS 'merge' SEP+;
LIST_PARTITION_KW: L_PAREN LIST_NS 'partition' SEP+;
LIST_POP_LEFT_KW: L_PAREN LIST_NS 'pop'US'left' SEP+;
LIST_POP_RIGHT_KW: L_PAREN LIST_NS 'pop'US'right' SEP+;
LIST_PUSH_LEFT_KW: L_PAREN LIST_NS 'push'US'left' SEP+;
LIST_PUSH_RIGHT_KW: L_PAREN LIST_NS 'push'US'right' SEP+;
LIST_RANGE_KW: L_PAREN LIST_NS 'range' SEP+;
LIST_REMOVE_ALL_KW: L_PAREN LIST_NS 'remove'US'all' SEP+;
LIST_REMOVE_AT_KW: L_PAREN LIST_NS 'remove'US('element'?US)?'at' SEP+;
LIST_REMOVE_ONE_KW: L_PAREN LIST_NS 'remove'US'one' SEP+;
LIST_REVERSE_KW: L_PAREN LIST_NS 'reverse'(US'list')? SEP+;
LIST_SAFE_INDEXED_MERGE_KW:
   L_PAREN LIST_NS (('safe'US'indexed')|('indexed'US'safe'))US'merge' SEP+;
LIST_SAFE_MERGE_KW: L_PAREN LIST_NS 'safe'US'merge' SEP+;
LIST_SET_KW: L_PAREN LIST_NS 'set' SEP+;
LIST_SHUFFLE_KW: L_PAREN LIST_NS 'shuffle' SEP+;
LIST_SIZE_KW: L_PAREN LIST_NS 'size' SEP+;
LIST_SORT_KW: L_PAREN LIST_NS 'sort' SEP+;
LIST_SUB_LIST_KW: L_PAREN LIST_NS 'sub'US'list' SEP+;
IMP_LIST_ADD_ALL_KW: L_PAREN LIST_NS 'add'US'all'(US'elements')?'!' SEP+;
IMP_LIST_ADD_AT_KW: L_PAREN LIST_NS 'add'(US'element')?US'at!' SEP+;
IMP_LIST_ADD_KW: L_PAREN LIST_NS 'add'(US'element')?'!' SEP+;
IMP_LIST_CLEAR_KW: L_PAREN LIST_NS 'clear!' SEP+;
IMP_LIST_FILTER_KW: L_PAREN LIST_NS 'filter!' SEP+;
IMP_LIST_INDEXED_FILTER_KW: L_PAREN LIST_NS 'indexed'US'filter!' SEP+;
IMP_LIST_INDEXED_MAP_KW: L_PAREN LIST_NS 'indexed'US'map!' SEP+;
IMP_LIST_INDEXED_MERGE_KW: L_PAREN LIST_NS 'indexed'US'merge!' SEP+;
IMP_LIST_INDEXED_PARTITION_KW: L_PAREN LIST_NS 'indexed'US'partition!' SEP+;
IMP_LIST_MAP_KW: L_PAREN LIST_NS 'map!' SEP+;
IMP_LIST_MERGE_KW: L_PAREN LIST_NS 'merge!' SEP+;
IMP_LIST_PARTITION_KW: L_PAREN LIST_NS 'partition!' SEP+;
IMP_LIST_POP_LEFT_KW: L_PAREN LIST_NS 'pop'US'left!' SEP+;
IMP_LIST_POP_RIGHT_KW: L_PAREN LIST_NS 'pop'US'right!' SEP+;
IMP_LIST_PUSH_LEFT_KW: L_PAREN LIST_NS 'push'US'left!' SEP+;
IMP_LIST_PUSH_RIGHT_KW: L_PAREN LIST_NS 'push'US'right!' SEP+;
IMP_LIST_REMOVE_ALL_KW: L_PAREN LIST_NS 'remove'US'all!' SEP+;
IMP_LIST_REMOVE_AT_KW: L_PAREN LIST_NS ('remove'US('elem''ent'?US)?'at!') SEP+;
IMP_LIST_REMOVE_ONE_KW: L_PAREN LIST_NS 'remove'US'one!' SEP+;
IMP_LIST_REVERSE_KW: L_PAREN LIST_NS 'reverse'(US'list')?'!' SEP+;
IMP_LIST_SAFE_INDEXED_MERGE_KW:
   L_PAREN LIST_NS (('indexed'US'safe')|('safe'US'indexed'))US'merge!' SEP+;
IMP_LIST_SAFE_MERGE_KW: L_PAREN LIST_NS 'safe'US'merge!' SEP+;
IMP_LIST_SET_KW: L_PAREN LIST_NS 'set!' SEP+;
IMP_LIST_SHUFFLE_KW: L_PAREN LIST_NS 'shuffle!' SEP+;
IMP_LIST_SORT_KW: L_PAREN LIST_NS 'sort!' SEP+;
IMP_LIST_SUB_LIST_KW: L_PAREN LIST_NS 'sub'US'list!' SEP+;




SET_KW: L_PAREN 'set' SEP+;
SET_ADD_ALL_KW: L_PAREN SET_NS 'add'US'all'(US'elements')? SEP+;
SET_ADD_KW: L_PAREN SET_NS 'add'(US'element')? SEP+;
SET_COUNT_KW: L_PAREN SET_NS 'count' SEP+;
SET_FILTER_KW: L_PAREN SET_NS 'filter' SEP+;
SET_FOLDL_KW: L_PAREN SET_NS foldl' SEP+;
SET_FOLDR_KW: L_PAREN SET_NS 'foldr' SEP+;
SET_GET_KW: L_PAREN SET_NS 'get' SEP+;
SET_INDEXED_FILTER_KW: L_PAREN SET_NS 'indexed'US'filter' SEP+;
SET_INDEXED_MAP_KW: L_PAREN SET_NS 'indexed'US'map' SEP+;
SET_INDEXED_MERGE_KW: L_PAREN SET_NS 'indexed'US'merge' SEP+;
SET_INDEXED_PARTITION_KW: L_PAREN SET_NS 'indexed'US'partition' SEP+;
SET_INDEX_OF_KW: L_PAREN SET_NS 'index'US'of' SEP+;
SET_IS_EMPTY_KW: L_PAREN SET_NS 'is'US'empty' SEP+;
SET_IS_MEMBER_KW: L_PAREN SET_NS ('is'US'member'|'contains'|'has') SEP+;
SET_MAP_KW: L_PAREN SET_NS 'map' SEP+;
SET_MERGE_KW: L_PAREN SET_NS 'merge' SEP+;
SET_PARTITION_KW: L_PAREN SET_NS 'partition' SEP+;
SET_POP_LEFT_KW: L_PAREN SET_NS 'pop'US'left' SEP+;
SET_POP_RIGHT_KW: L_PAREN SET_NS 'pop'US'right' SEP+;
SET_RANGE_KW: L_PAREN SET_NS 'range' SEP+;
SET_REMOVE_AT_KW: L_PAREN SET_NS ('remove'US('elem'('ent')?US)?'at') SEP+;
SET_REMOVE_ONE_KW: L_PAREN SET_NS 'remove'US'one' SEP+;
SET_SAFE_INDEXED_MERGE_KW:
   L_PAREN SET_NS (('safe'US'indexed')|('indexed'US'safe'))US'merge' SEP+;
SET_SAFE_MERGE_KW: L_PAREN SET_NS 'safe'US'merge' SEP+;
SET_SET_KW: L_PAREN SET_NS 'set' SEP+;
SET_SIZE_KW: L_PAREN SET_NS 'size' SEP+;
IMP_SET_ADD_ALL_KW: L_PAREN SET_NS 'add'US'all'(US'elements')?'!' SEP+;
IMP_SET_ADD_KW: L_PAREN SET_NS 'add'(US'element')?'!' SEP+;
IMP_SET_CLEAR_KW: L_PAREN SET_NS 'clear!' SEP+;
IMP_SET_FILTER_KW: L_PAREN SET_NS 'filter!' SEP+;
IMP_SET_INDEXED_FILTER_KW: L_PAREN SET_NS 'indexed'US'filter!' SEP+;
IMP_SET_INDEXED_MAP_KW: L_PAREN SET_NS 'indexed'US'map!' SEP+;
IMP_SET_INDEXED_MERGE_KW: L_PAREN SET_NS 'indexed'US'merge!' SEP+;
IMP_SET_INDEXED_PARTITION_KW: L_PAREN SET_NS 'indexed'US'partition!' SEP+;
IMP_SET_MAP_KW: L_PAREN SET_NS 'map!' SEP+;
IMP_SET_MERGE_KW: L_PAREN SET_NS 'merge!' SEP+;
IMP_SET_PARTITION_KW: L_PAREN SET_NS 'partition!' SEP+;
IMP_SET_POP_LEFT_KW: L_PAREN SET_NS 'pop'US'left!' SEP+;
IMP_SET_POP_RIGHT_KW: L_PAREN SET_NS 'pop'US'right!' SEP+;
IMP_SET_REMOVE_AT_KW: L_PAREN SET_NS ('remove'US('elem'('ent')?US)?'at!') SEP+;
IMP_SET_REMOVE_ONE_KW: L_PAREN SET_NS 'remove'US'one!' SEP+;
IMP_SET_SAFE_INDEXED_MERGE_KW:
   L_PAREN SET_NS (('indexed'US'safe')|('safe'US'indexed'))US'merge!' SEP+;
IMP_SET_SAFE_MERGE_KW: L_PAREN SET_NS 'safe'US'merge!' SEP+;




AND_KW: L_PAREN ('and'|'/\\') SEP+;
FALSE_KW: L_PAREN 'false' SEP* R_PAREN;
IMPLIES_KW: L_PAREN ('implies'|'=>'|'->') SEP+;
NOT_KW: L_PAREN ('not'|'~'|'!') SEP+;
ONE_IN_KW: L_PAREN ('exactly'US)?'one'(US'in')? SEP+;
OR_KW: L_PAREN ('or'|'\\/') SEP+;
TRUE_KW: L_PAREN 'true' SEP* R_PAREN;




ENABLE_TEXT_EFFECT_KW: L_PAREN 'text'US'effect' SEP+;
NEWLINE_KW: L_PAREN 'newline' SEP* R_PAREN;
TEXT_JOIN_KW: L_PAREN TEXT_NS 'join' SEP+;
TEXT_KW: L_PAREN 'text' SEP+;







CONS_KW: L_PAREN 'cons' SEP+;
CAR_KW: L_PAREN 'car' SEP+;
CDR_KW: L_PAREN 'cdr' SEP+;




COND_KW: L_PAREN 'cond' SEP+;
DO_WHILE_KW: L_PAREN 'do'US'while' SEP+;
FOR_KW: L_PAREN 'for' SEP+;
FOR_EACH_KW: L_PAREN 'for'US'each' SEP+;
WHILE_KW: L_PAREN 'while' SEP+;
SWITCH_KW: L_PAREN 'switch' SEP+;
IMP_BREAK_KW: L_PAREN 'break'('!'?) SEP* R_PAREN;
IMP_CONTINUE_KW: L_PAREN 'continue'('!'?) SEP* R_PAREN;




IF_ELSE_KW: L_PAREN 'if'US'else' SEP+;
IF_KW: L_PAREN 'if' SEP+;




STRING_KW: L_PAREN 'string' SEP+;




DEFAULT_KW: L_PAREN 'default' SEP+;
CAST_KW: L_PAREN 'cast' SEP+;




EQUALS_KW: L_PAREN ('equals'|'='|'=='|'eq') SEP+;
GREATER_EQUAL_THAN_KW: L_PAREN (('greater'US'equal'US'than')|'>='|'ge') SEP+;
GREATER_THAN_KW: L_PAREN (('greater'US'than')|'>'|'gt') SEP+;
LOWER_EQUAL_THAN_KW: L_PAREN (('lower'US'equal'US'than')|'=<'|'<='|'le') SEP+;
LOWER_THAN_KW: L_PAREN (('lower'US'than')|'<'|'lt') SEP+;




IMP_ASSERT_KW: L_PAREN 'assert!' SEP+;
IGNORE_ERROR_KW: L_PAREN 'ignore'US('error'|'warning') SEP+;




EXTENSION_FIRST_LEVEL_KW: L_PAREN '@';
EXTRA_INSTRUCTION_KW: L_PAREN '#';
EXTRA_COMPUTATION_KW: L_PAREN '$';




FIELD_KW: L_PAREN STRUCT_NS ('get'US)?'field' SEP+;
SET_FIELDS_KW: L_PAREN STRUCT_NS 'set'US'fields' SEP+;
IMP_SET_FIELDS_KW: L_PAREN STRUCT_NS 'set'US'fields!' SEP+;




PLAYER_CHOICE_KW:
   L_PAREN ('choice'|'user'US'choice'|'player'US'choice')'!' SEP+;
TEXT_OPTION_KW: L_PAREN ('option'|'user'US'option'|'player'US'option') SEP+;
EVENT_OPTION_KW: L_PAREN ('event'|'user'US'event'|'player'US'event') SEP+;
PROMPT_STRING_KW: L_PAREN 'prompt'US'str''ing'?'!' SEP+;
PROMPT_INTEGER_KW: L_PAREN 'prompt'US'int''eger'?'!' SEP+;




LET_KW: L_PAREN 'let' SEP+;
AT_KW: L_PAREN 'at' SEP+;
REF_KW:
   L_PAREN
      (
         ((('ref''erence'?)|'ptr'|'pointer')(US'to')?)
         |('addr''ess'?(US'of')?)
      )
   SEP+;
IMP_SET_KW: L_PAREN 'set'(US(('val''ue'?)|('var''iable'?)))?'!' SEP+;
VARIABLE_KW: L_PAREN 'var''iable'? SEP+;




ALLOCATE_KW: L_PAREN (('alloc''ate'?)|'malloc'|'new')'!' SEP+;
FREE_KW: L_PAREN ('free!'|'release!'|'destroy!') SEP+;




LAMBDA_KW: L_PAREN 'lambda' SEP+;
EVAL_KW: L_PAREN 'eval''uate'? SEP+;








SEQUENCE_KW: L_PAREN 'seq''uence'? SEP+;
DONE_KW: L_PAREN 'done''!'? SEP* R_PAREN;
VISIT_KW:
   L_PAREN ('call'|'visit')(US(('seq''uence'?)|('proc''edure'?)))?'!' SEP+;
CONTINUE_AS_KW:
   L_PAREN
      (('continue'US('as'|'to'|'with'))|('jump'(US'to')?)|('go'US'to')|'exec')
      (US(('seq''uence'?)|('proc''edure'?)))?
      '!'
   SEP+;
END_KW: L_PAREN 'end'('!'?) SEP* R_PAREN;




WORD: ((~([ \t\r\n()]))|'(lp)'|'(rp)'|'(sp)')+
   {
      setText
      (
         getText().replaceAll
         (
            "\\(sp\\)",
            " "
         ).replaceAll
         (
            "\\(lp\\)",
            "("
         ).replaceAll
         (
            "\\(rp\\)",
            ")"
         )
      );
   };

COMMENT: WS* ';;' .*? '\n' -> channel(HIDDEN);
