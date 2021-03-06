lexer grammar FateLexer;

@header
{
   package tonkadur.fate.v1.parser;
}

fragment SEP: [ \t\r\n]+;
fragment US: '_'?;


WS: SEP;

L_PAREN: '(';
R_PAREN: ')';


ABS_KW: L_PAREN 'abs'('olute'?) SEP+;
ACCESS_KW: L_PAREN 'access' SEP+;
ACCESS_POINTER_KW: L_PAREN 'access'US('ptr'|'pointer') SEP+;
ADD_KW: L_PAREN 'add'(US'element')? SEP+;
IMP_ADD_KW: L_PAREN 'add'(US'element')?'!' SEP+;
ADD_AT_KW: L_PAREN 'add'(US'element')?US'at' SEP+;
IMP_ADD_AT_KW: L_PAREN 'add'(US'element')?US'at!' SEP+;
ADD_ALL_KW: L_PAREN 'add'US'all'(US'elements')? SEP+;
IMP_ADD_ALL_KW: L_PAREN 'add'US'all'(US'elements')?'!' SEP+;
AND_KW: L_PAREN ('and'|'/\\') SEP+;
IMP_ASSERT_KW: L_PAREN 'assert!' SEP+;
AT_KW: L_PAREN 'at' SEP+;
IMP_BREAK_KW: L_PAREN 'break'('!'?) SEP* R_PAREN;
CAR_KW: L_PAREN 'car' SEP+;
CAST_KW: L_PAREN 'cast' SEP+;
CDR_KW: L_PAREN 'cdr' SEP+;
IMP_CLEAR_KW: L_PAREN 'clear!' SEP+;
COND_KW: L_PAREN 'cond' SEP+;
CONS_KW: L_PAREN 'cons' SEP+;
COUNT_KW: L_PAREN 'count' SEP+;
DECLARE_ALIAS_TYPE_KW:
   L_PAREN ((('declare'|'define'|'def')US(('sub'|'alias')US)?'type')|'typedef') SEP+;
DECLARE_DICT_TYPE_KW: L_PAREN
   ('declare'|'define'|'def')US('dict'|('struct''ure'?))(US'type')? SEP+;
DECLARE_EXTRA_INSTRUCTION_KW: L_PAREN ('declare'|'define'|'def')US'extra'US'instruction' SEP+;
DECLARE_EXTRA_COMPUTATION_KW: L_PAREN ('declare'|'define'|'def')US'extra'US'computation' SEP+;
DECLARE_EXTRA_TYPE_KW: L_PAREN ('declare'|'define'|'def')US'extra'US'type' SEP+;
DECLARE_EVENT_TYPE_KW: L_PAREN ('declare'|'define'|'def')(US'input')?US'event'(US'type')? SEP+;
DECLARE_TEXT_EFFECT_KW: L_PAREN ('declare'|'define'|'def')US'text'US'effect' SEP+;
DECLARE_VARIABLE_KW: L_PAREN 'global' SEP+;
LOCAL_KW: L_PAREN 'local' SEP+;
EXTERNAL_KW: L_PAREN 'extern'('al'?) SEP+;
DEFAULT_KW: L_PAREN 'default' SEP+;
DEFINE_SEQUENCE_KW: L_PAREN ('declare'|'define'|'def')US(('seq'('uence')?)|('proc'('edure'?))) SEP+;
DIVIDE_KW: L_PAREN ('divide'|'/'|'div') SEP+;
DO_WHILE_KW: L_PAREN ('do'US'while') SEP+;
ENABLE_TEXT_EFFECT_KW: L_PAREN 'text'US'effect' SEP+;
END_KW: L_PAREN 'end'('!'?) SEP* R_PAREN;
EQUALS_KW: L_PAREN ('equals'|'='|'=='|'eq') SEP+;
EXTENSION_FIRST_LEVEL_KW: L_PAREN '@';
EXTRA_INSTRUCTION_KW: L_PAREN '#';
JOIN_KW: L_PAREN ('text'US)? 'join' SEP+;
EXTRA_COMPUTATION_KW: L_PAREN '$';
FALSE_KW: L_PAREN 'false' SEP* R_PAREN;
IGNORE_ERROR_KW: L_PAREN 'ignore'US('error'|'warning') SEP+;
IMP_IGNORE_ERROR_KW: L_PAREN 'ignore'US('error'|'warning')'!' SEP+;
FATE_VERSION_KW: L_PAREN 'fate'US'version' SEP+;
FIELD_KW: L_PAREN 'field' SEP+;
FIELD_ACCESS_KW: L_PAREN (('get'US'field')|('field'US'access')) SEP+;
FILTER_KW: L_PAREN 'filter' SEP+;
INDEXED_FILTER_KW: L_PAREN 'indexed'US'filter' SEP+;
IMP_FILTER_KW: L_PAREN 'filter!' SEP+;
IMP_INDEXED_FILTER_KW: L_PAREN 'indexed'US'filter!' SEP+;
FOR_EACH_KW: L_PAREN ('for'US'each') SEP+;
FOR_KW: L_PAREN 'for' SEP+;
FOLDR_KW: L_PAREN 'foldr' SEP+;
FOLDL_KW: L_PAREN 'foldl' SEP+;
FREE_KW: L_PAREN ('free!'|'release!'|'destroy!') SEP+;
GREATER_EQUAL_THAN_KW: L_PAREN ('greater'US'equal'US'than'|'>='|'ge') SEP+;
GREATER_THAN_KW: L_PAREN ('greater'US'than'|'>'|'gt') SEP+;
IF_ELSE_KW: L_PAREN ('if'US'else') SEP+;
IF_KW: L_PAREN 'if' SEP+;
IMPLIES_KW: L_PAREN ('implies'|'=>'|'->') SEP+;
INCLUDE_KW: L_PAREN 'include' SEP+;
INDEX_OF_KW: L_PAREN ('index'US'of') SEP+;
INDEXED_MAP_KW: L_PAREN 'indexed'US'map' SEP+;
IMP_INDEXED_MAP_KW: L_PAREN 'indexed'US'map!' SEP+;
IS_MEMBER_KW: L_PAREN ('is'US'member'|'contains'|'has') SEP+;
IS_EMPTY_KW: L_PAREN 'is'US'empty' SEP+;
LOWER_EQUAL_THAN_KW: L_PAREN ('lower'US'equal'US'than'|'=<'|'<='|'le') SEP+;
LOWER_THAN_KW: L_PAREN ('lower'US'than'|'<'|'lt') SEP+;
LET_KW: L_PAREN 'let' SEP+;
MINUS_KW: L_PAREN ('minus'|'-') SEP+;
MIN_KW: L_PAREN ('min'('imum'?)) SEP+;
MAP_KW: L_PAREN 'map' SEP+;
STRING_KW: L_PAREN 'string' SEP+;
IMP_MAP_KW: L_PAREN 'map!' SEP+;
MAX_KW: L_PAREN ('max'('imum'?)) SEP+;
LAMBDA_KW: L_PAREN 'lambda' SEP+;
EVAL_KW: L_PAREN 'eval'('uate'?) SEP+;
CLAMP_KW: L_PAREN ('clamp') SEP+;
MODULO_KW: L_PAREN ('modulo'|'%'|'mod') SEP+;
MERGE_TO_LIST_KW : L_PAREN 'merge'US'to'US'list' SEP+;
MERGE_TO_SET_KW : L_PAREN 'merge'US'to'US'set' SEP+;
INDEXED_MERGE_TO_LIST_KW : L_PAREN 'indexed'US'merge'US'to'US'list' SEP+;
INDEXED_MERGE_TO_SET_KW : L_PAREN 'indexed'US'merge'US'to'US'set' SEP+;
IMP_MERGE_KW : L_PAREN 'merge!' SEP+;
IMP_INDEXED_MERGE_KW : L_PAREN 'indexed'US'merge!' SEP+;
SAFE_MERGE_TO_LIST_KW : L_PAREN 'safe'US'merge'US'to'US'list' SEP+;
SAFE_MERGE_TO_SET_KW : L_PAREN 'safe'US'merge'US'to'US'set' SEP+;
SAFE_INDEXED_MERGE_TO_LIST_KW : L_PAREN (('safe'US'indexed')|('indexed'US'safe'))US'merge'US'to'US'list' SEP+;
SAFE_INDEXED_MERGE_TO_SET_KW : L_PAREN (('safe'US'indexed')|('indexed'US'safe'))US'merge'US'to'US'set' SEP+;
SAFE_IMP_MERGE_KW : L_PAREN 'safe'US'merge!' SEP+;
SAFE_IMP_INDEXED_MERGE_KW : L_PAREN (('indexed'US'safe')|('safe'US'indexed'))US'merge!' SEP+;
NEWLINE_KW: L_PAREN 'newline' SEP* R_PAREN;
ALLOCATE_KW: L_PAREN (('alloc''ate'?)|'malloc'|'new')'!' SEP+;
NOT_KW: L_PAREN ('not'|'~'|'!') SEP+;
ONE_IN_KW: L_PAREN ('exactly'US)?'one'(US'in')? SEP+;
OR_KW: L_PAREN ('or'|'\\/') SEP+;
TEXT_KW: L_PAREN 'text' SEP+;
PARTITION_KW: L_PAREN 'partition' SEP+;
IMP_PARTITION_KW: L_PAREN 'partition!' SEP+;
INDEXED_PARTITION_KW: L_PAREN 'indexed'US'partition' SEP+;
IMP_INDEXED_PARTITION_KW: L_PAREN 'indexed'US'partition!' SEP+;
POP_LEFT_KW: L_PAREN 'pop'US'left' SEP+;
IMP_POP_LEFT_KW: L_PAREN 'pop'US'left!' SEP+;
POP_RIGHT_KW: L_PAREN 'pop'US'right' SEP+;
IMP_POP_RIGHT_KW: L_PAREN 'pop'US'right!' SEP+;
PUSH_LEFT_KW: L_PAREN 'push'US'left' SEP+;
IMP_PUSH_LEFT_KW: L_PAREN 'push'US'left!' SEP+;
PUSH_RIGHT_KW: L_PAREN 'push'US'right' SEP+;
IMP_PUSH_RIGHT_KW: L_PAREN 'push'US'right!' SEP+;
PLAYER_CHOICE_KW: L_PAREN ('choice'|'user'US'choice'|'player'US'choice')'!' SEP+;
TEXT_OPTION_KW: L_PAREN ('option'|'user'US'option'|'player'US'option') SEP+;
EVENT_OPTION_KW: L_PAREN ('event'|'user'US'event'|'player'US'event') SEP+;
PLUS_KW: L_PAREN ('plus'|'+') SEP+;
POWER_KW: L_PAREN ('power'|'^'|'**'|'pow') SEP+;
RANGE_KW: L_PAREN 'range' SEP+;
RANDOM_KW: L_PAREN ('random'|'rand'|'rnd') SEP+;
REF_KW: L_PAREN (((('ref'('erence'?))|'ptr'|'pointer')(US'to')?)|('addr'('ess'?)(US'of')?)) SEP+;
REMOVE_ALL_KW: L_PAREN 'remove'US'all' SEP+;
IMP_REMOVE_ALL_KW: L_PAREN 'remove'US'all!' SEP+;
REVERSE_KW: L_PAREN 'reverse'(US'list')? SEP+;
IMP_REVERSE_KW: L_PAREN 'reverse'(US'list')?'!' SEP+;
REMOVE_ONE_KW: L_PAREN 'remove'US'one' SEP+;
IMP_REMOVE_ONE_KW: L_PAREN 'remove'US'one!' SEP+;
REMOVE_AT_KW: L_PAREN ('remove'US('elem'('ent')?US)?'at') SEP+;
IMP_REMOVE_AT_KW: L_PAREN ('remove'US('elem'('ent')?US)?'at!') SEP+;
REQUIRE_EXTENSION_KW: L_PAREN 'require'US'extension' SEP+;
REQUIRE_KW: L_PAREN 'require' SEP+;
PROMPT_STRING_KW: L_PAREN 'prompt_string!' SEP+;
PROMPT_INTEGER_KW: L_PAREN 'prompt_int'('eger'?)'!' SEP+;
SHUFFLE_KW: L_PAREN 'shuffle' SEP+;
IMP_SHUFFLE_KW: L_PAREN 'shuffle!' SEP+;
SORT_KW: L_PAREN 'sort' SEP+;
IMP_SORT_KW: L_PAREN 'sort!' SEP+;
SET_FIELDS_KW: L_PAREN 'set'US'fields' SEP+;
IMP_SET_FIELDS_KW: L_PAREN 'set'US'fields!' SEP+;
IMP_SET_KW: L_PAREN 'set'(US(('val''ue'?)|('var''iable'?)))?'!' SEP+;
SUB_LIST_KW: L_PAREN 'sub'US'list' SEP+;
IMP_SUB_LIST_KW: L_PAREN 'sub'US'list!' SEP+;
LIST_KW: L_PAREN 'list' SEP+;
SIZE_KW: L_PAREN 'size' SEP+;
SEQUENCE_KW: L_PAREN ('seq'|'sequence') SEP+;
SWITCH_KW: L_PAREN 'switch' SEP+;
TIMES_KW: L_PAREN ('times'|'*') SEP+;
TRUE_KW: L_PAREN 'true' SEP* R_PAREN;
DONE_KW: L_PAREN 'done''!'? SEP* R_PAREN;
SET_KW: L_PAREN 'set' SEP+;
VARIABLE_KW: L_PAREN ('variable'|'var') SEP+;
VISIT_KW: L_PAREN ('call'|'visit')(US(('seq'('uence'?))|('proc'('edure'?))))?'!' SEP+;
CONTINUE_AS_KW: L_PAREN (('continue'US('as'|'to'|'with'))|('jump'(US'to')?)|('go'US'to')|'exec')(US(('seq'('uence'?))|('proc'('edure'?))))?'!' SEP+;
WHILE_KW: L_PAREN 'while' SEP+;

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
