export class LabelMappingUtils {

  public static codeDescriptionToLabel(item: {code: string, description: string}): string {
    return (item.description != null ? item.description + ' - ' : '') + item.code;
  }

  public static nameDescriptionToLabel(item: {name: string, description: string}): string {
    return (item.description != null ? item.description + ' - ' : '') + item.name;
  }

  public static nameToLabel(item: {name: string}): string {
    return item.name;
  }

  public static descriptionToLabel(item: {description: string}): string {
    return item.description;
  }

  public static sectorToLabel(item: boolean): string {
    return item ? 'Private' : 'Public';
  }
}
