namespace java com.crowdflower

struct SubStruct {
    1: set<i32> numbers
}

struct IntAndString {
    1: required string name
    2: i32 count
    5: map<string,string> flexMetaData
    8: i64 numberOfAtoms
    11: SubStruct hello
}